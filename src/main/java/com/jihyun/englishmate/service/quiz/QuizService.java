package com.jihyun.englishmate.service.quiz;

import com.jihyun.englishmate.dto.quiz.QuizAnswerRequest;
import com.jihyun.englishmate.dto.quiz.QuizAnswerResponse;
import com.jihyun.englishmate.dto.quiz.QuizQuestionResponse;
import com.jihyun.englishmate.dto.quiz.QuizQuestionResultResponse;
import com.jihyun.englishmate.dto.quiz.QuizResultResponse;
import com.jihyun.englishmate.dto.quiz.QuizScopeItemResponse;
import com.jihyun.englishmate.dto.quiz.QuizStartRequest;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.quiz.QuizAttempt;
import com.jihyun.englishmate.entity.quiz.QuizAttemptStudyMaterial;
import com.jihyun.englishmate.entity.quiz.QuizQuestionResult;
import com.jihyun.englishmate.entity.quiz.QuizType;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.repository.quiz.QuizAttemptRepository;
import com.jihyun.englishmate.repository.quiz.QuizAttemptStudyMaterialRepository;
import com.jihyun.englishmate.repository.quiz.QuizQuestionResultRepository;
import com.jihyun.englishmate.repository.vocabulary.VocabularyRepository;
import com.jihyun.englishmate.util.word.WordNormalizer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 퀴즈 출제, 진행, 채점, 결과 조회를 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private static final int MULTIPLE_CHOICE_COUNT = 4;

    private final StudyMaterialRepository studyMaterialRepository;
    private final MemberRepository memberRepository;
    private final VocabularyRepository vocabularyRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptStudyMaterialRepository quizAttemptStudyMaterialRepository;
    private final QuizQuestionResultRepository quizQuestionResultRepository;
    private final EntityManager entityManager;

    /**
     * 로그인한 사용자의 학습 자료를 퀴즈 범위 선택 DTO로 조회합니다.
     */
    public List<QuizScopeItemResponse> findScopeItems(Long memberId) {
        return studyMaterialRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(QuizScopeItemResponse::from)
                .toList();
    }

    /**
     * 선택 범위에 포함된 유효 단어로 퀴즈를 시작합니다.
     */
    @Transactional
    public Long start(Long memberId, QuizStartRequest request) {
        QuizType quizType = request.quizType();
        if (quizType == null) {
            throw new IllegalArgumentException("퀴즈 유형을 선택해주세요.");
        }

        List<StudyMaterial> selectedMaterials = resolveSelectedMaterials(memberId, request);
        List<Long> selectedMaterialIds = selectedMaterials.stream()
                .map(StudyMaterial::getId)
                .toList();
        List<QuizWord> quizWords = findQuizWords(memberId, selectedMaterialIds);
        validateQuestionPool(quizType, quizWords);

        Collections.shuffle(quizWords);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        QuizAttempt quizAttempt = quizAttemptRepository.save(QuizAttempt.start(member, quizType, quizWords.size()));

        List<QuizAttemptStudyMaterial> scopes = selectedMaterials.stream()
                .map(material -> QuizAttemptStudyMaterial.create(quizAttempt, material))
                .toList();
        quizAttemptStudyMaterialRepository.saveAll(scopes);

        List<QuizQuestionResult> questions = createQuestions(quizAttempt, quizType, quizWords);
        quizQuestionResultRepository.saveAll(questions);

        return quizAttempt.getId();
    }

    /**
     * 현재 문제를 조회합니다.
     */
    public QuizQuestionResponse findQuestion(Long memberId, Long attemptId, int questionOrder) {
        QuizAttempt attempt = findAttempt(memberId, attemptId);
        if (questionOrder < 1 || questionOrder > attempt.getTotalQuestions()) {
            throw new IllegalArgumentException("존재하지 않는 문제입니다.");
        }

        QuizQuestionResult question = quizQuestionResultRepository
                .findByQuizAttemptIdAndQuestionOrder(attemptId, questionOrder)
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));
        int expectedOrder = quizQuestionResultRepository.countByQuizAttemptIdAndAnsweredAtIsNotNull(attemptId) + 1;
        if (!question.isAnswered() && !attempt.isCompleted() && questionOrder != expectedOrder) {
            throw new IllegalArgumentException("현재 순서의 문제만 확인할 수 있습니다.");
        }
        int progressPercent = (int) Math.round((questionOrder * 100.0) / attempt.getTotalQuestions());

        return new QuizQuestionResponse(
                attempt.getId(),
                question.getQuestionOrder(),
                attempt.getTotalQuestions(),
                progressPercent,
                attempt.getQuizType(),
                attempt.getQuizType().getLabel(),
                question.getQuestionText(),
                List.copyOf(question.getChoices()),
                attempt.getQuizType().isMultipleChoice(),
                question.isAnswered(),
                question.getSubmittedAnswer(),
                question.isAnswered() ? question.getCorrectAnswer() : null,
                question.getCorrect()
        );
    }

    /**
     * 제출 답안을 채점하고 결과를 저장합니다.
     */
    @Transactional
    public QuizAnswerResponse submitAnswer(
            Long memberId,
            Long attemptId,
            int questionOrder,
            QuizAnswerRequest request
    ) {
        QuizAttempt attempt = findAttempt(memberId, attemptId);
        if (attempt.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 퀴즈입니다.");
        }

        QuizQuestionResult question = quizQuestionResultRepository
                .findByQuizAttemptIdAndQuestionOrder(attemptId, questionOrder)
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));
        if (question.isAnswered()) {
            throw new IllegalArgumentException("이미 답안을 제출한 문제입니다.");
        }

        int expectedOrder = quizQuestionResultRepository.countByQuizAttemptIdAndAnsweredAtIsNotNull(attemptId) + 1;
        if (questionOrder != expectedOrder) {
            throw new IllegalArgumentException("현재 순서의 문제만 제출할 수 있습니다.");
        }

        String submittedAnswer = normalizeSpaces(request.submittedAnswer());
        if (submittedAnswer.isBlank()) {
            throw new IllegalArgumentException("답안을 입력하거나 선택해주세요.");
        }
        if (attempt.getQuizType().isMultipleChoice() && !question.getChoices().contains(submittedAnswer)) {
            throw new IllegalArgumentException("유효하지 않은 보기입니다.");
        }

        boolean correct = grade(attempt.getQuizType(), submittedAnswer, question.getCorrectAnswer());
        question.submit(submittedAnswer, correct);
        attempt.recordAnswer(correct);

        boolean lastQuestion = questionOrder == attempt.getTotalQuestions();
        if (lastQuestion) {
            attempt.complete();
        }

        return new QuizAnswerResponse(correct, submittedAnswer, question.getCorrectAnswer(), lastQuestion);
    }

    /**
     * 완료 여부와 관계없이 현재까지의 퀴즈 결과를 조회합니다.
     */
    public QuizResultResponse findResult(Long memberId, Long attemptId) {
        QuizAttempt attempt = findAttempt(memberId, attemptId);
        if (!attempt.isCompleted()) {
            throw new IllegalArgumentException("아직 완료되지 않은 퀴즈입니다.");
        }

        List<String> materialTitles = quizAttemptStudyMaterialRepository.findMaterialTitlesByAttemptId(attemptId);
        List<QuizQuestionResultResponse> questionResults = quizQuestionResultRepository
                .findAllByQuizAttemptIdOrderByQuestionOrderAsc(attemptId)
                .stream()
                .map(question -> new QuizQuestionResultResponse(
                        question.getQuestionOrder(),
                        question.getQuestionText(),
                        question.getSubmittedAnswer(),
                        question.getCorrectAnswer(),
                        question.getCorrect()
                ))
                .toList();
        int correctRate = attempt.getTotalQuestions() == 0
                ? 0
                : (int) Math.round((attempt.getCorrectCount() * 100.0) / attempt.getTotalQuestions());

        return new QuizResultResponse(
                attempt.getId(),
                attempt.getQuizType(),
                attempt.getQuizType().getLabel(),
                materialTitles,
                attempt.getTotalQuestions(),
                attempt.getCorrectCount(),
                attempt.getWrongCount(),
                correctRate,
                questionResults
        );
    }

    private List<StudyMaterial> resolveSelectedMaterials(Long memberId, QuizStartRequest request) {
        if (request.selectAllMaterials()) {
            List<StudyMaterial> materials = studyMaterialRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
            if (materials.isEmpty()) {
                throw new IllegalArgumentException("등록된 학습 자료가 없습니다.");
            }
            return materials;
        }

        List<Long> selectedIds = request.selectedStudyMaterialIds();
        if (selectedIds.isEmpty()) {
            throw new IllegalArgumentException("학습 자료를 하나 이상 선택해주세요.");
        }

        List<StudyMaterial> materials = studyMaterialRepository.findAllByMemberIdAndIdIn(memberId, selectedIds);
        if (materials.size() != selectedIds.stream().distinct().count()) {
            throw new IllegalArgumentException("선택할 수 없는 학습 자료가 포함되어 있습니다.");
        }
        return materials;
    }

    private List<QuizWord> findQuizWords(Long memberId, List<Long> selectedMaterialIds) {
        Map<Long, QuizWord> uniqueQuizWords = new LinkedHashMap<>();
        for (Vocabulary vocabulary : vocabularyRepository.findQuizTargets(memberId, selectedMaterialIds)) {
            String meaning = normalizeSpaces(vocabulary.getMeaning());
            if (!meaning.isBlank()) {
                uniqueQuizWords.putIfAbsent(
                        vocabulary.getWord().getId(),
                        new QuizWord(
                                vocabulary.getWord().getId(),
                                vocabulary.getWord().getText(),
                                vocabulary.getWord().getNormalizedText(),
                                meaning
                        )
                );
            }
        }
        return new ArrayList<>(uniqueQuizWords.values());
    }

    private void validateQuestionPool(QuizType quizType, List<QuizWord> quizWords) {
        if (quizWords.isEmpty()) {
            throw new IllegalArgumentException("선택한 범위에 단어장에 저장되고 의미가 입력된 단어가 없습니다.");
        }
        if (quizType.isWritten()) {
            return;
        }

        long optionValueCount = quizWords.stream()
                .map(word -> optionAnswer(quizType, word))
                .filter(value -> !value.isBlank())
                .distinct()
                .count();
        if (quizWords.size() < MULTIPLE_CHOICE_COUNT || optionValueCount < MULTIPLE_CHOICE_COUNT) {
            throw new IllegalArgumentException("객관식 퀴즈는 서로 다른 보기 값이 최소 4개 필요합니다.");
        }
    }

    private List<QuizQuestionResult> createQuestions(
            QuizAttempt quizAttempt,
            QuizType quizType,
            List<QuizWord> quizWords
    ) {
        List<QuizQuestionResult> questions = new ArrayList<>();
        for (int index = 0; index < quizWords.size(); index++) {
            QuizWord quizWord = quizWords.get(index);
            List<String> choices = quizType.isMultipleChoice()
                    ? createChoices(quizType, quizWord, quizWords)
                    : List.of();

            Word word = entityManager.getReference(Word.class, quizWord.wordId());
            questions.add(QuizQuestionResult.create(
                    quizAttempt,
                    word,
                    index + 1,
                    questionText(quizType, quizWord),
                    optionAnswer(quizType, quizWord),
                    choices
            ));
        }
        return questions;
    }

    private List<String> createChoices(QuizType quizType, QuizWord correctWord, List<QuizWord> quizWords) {
        String correctAnswer = optionAnswer(quizType, correctWord);
        List<String> wrongChoices = quizWords.stream()
                .map(word -> optionAnswer(quizType, word))
                .filter(value -> !value.isBlank())
                .filter(value -> !value.equals(correctAnswer))
                .distinct()
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        Collections.shuffle(wrongChoices);

        if (wrongChoices.size() < MULTIPLE_CHOICE_COUNT - 1) {
            throw new IllegalArgumentException("객관식 보기를 만들 수 있는 단어가 부족합니다.");
        }

        List<String> choices = new ArrayList<>();
        choices.add(correctAnswer);
        choices.addAll(wrongChoices.subList(0, MULTIPLE_CHOICE_COUNT - 1));
        Collections.shuffle(choices);
        return choices;
    }

    private String questionText(QuizType quizType, QuizWord quizWord) {
        return quizType.isWordQuestion() ? quizWord.text() : quizWord.meaning();
    }

    private String optionAnswer(QuizType quizType, QuizWord quizWord) {
        return quizType.isWordQuestion() ? quizWord.meaning() : quizWord.text();
    }

    private boolean grade(QuizType quizType, String submittedAnswer, String correctAnswer) {
        if (quizType.isMultipleChoice()) {
            return submittedAnswer.equals(correctAnswer);
        }
        if (quizType == QuizType.MEANING_TO_WORD_WRITTEN) {
            return normalizeEnglishAnswer(submittedAnswer).equals(normalizeEnglishAnswer(correctAnswer));
        }
        return isMeaningAnswerCorrect(submittedAnswer, correctAnswer);
    }

    private String normalizeEnglishAnswer(String value) {
        return WordNormalizer.normalize(normalizeSpaces(value));
    }

    private boolean isMeaningAnswerCorrect(String submittedAnswer, String correctAnswer) {
        String normalizedSubmittedAnswer = normalizeKoreanMeaning(submittedAnswer);
        for (String answer : correctAnswer.split(",")) {
            if (normalizedSubmittedAnswer.equals(normalizeKoreanMeaning(answer))) {
                return true;
            }
        }
        return false;
    }

    private String normalizeKoreanMeaning(String value) {
        return normalizeSpaces(value).toLowerCase(Locale.ROOT);
    }

    private String normalizeSpaces(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    private QuizAttempt findAttempt(Long memberId, Long attemptId) {
        return quizAttemptRepository.findByIdAndMemberId(attemptId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈를 찾을 수 없습니다."));
    }

    private record QuizWord(
            Long wordId,
            String text,
            String normalizedText,
            String meaning
    ) {
    }
}
