package com.jihyun.englishmate.service.review;

import com.jihyun.englishmate.dto.review.ReviewAnswerRequest;
import com.jihyun.englishmate.dto.review.ReviewCardResponse;
import com.jihyun.englishmate.dto.review.ReviewCardResultResponse;
import com.jihyun.englishmate.dto.review.ReviewCompleteResponse;
import com.jihyun.englishmate.dto.review.ReviewScopeItemResponse;
import com.jihyun.englishmate.dto.review.ReviewStartRequest;
import com.jihyun.englishmate.dto.vocabulary.VocabularySourceResponse;
import com.jihyun.englishmate.dto.vocabulary.VocabularySourceRow;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.review.LearningProgress;
import com.jihyun.englishmate.entity.review.ReviewCardResult;
import com.jihyun.englishmate.entity.review.ReviewResponseType;
import com.jihyun.englishmate.entity.review.ReviewSession;
import com.jihyun.englishmate.entity.review.ReviewSessionStudyMaterial;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.repository.review.LearningProgressRepository;
import com.jihyun.englishmate.repository.review.ReviewCardResultRepository;
import com.jihyun.englishmate.repository.review.ReviewSessionRepository;
import com.jihyun.englishmate.repository.review.ReviewSessionStudyMaterialRepository;
import com.jihyun.englishmate.repository.vocabulary.VocabularyRepository;
import com.jihyun.englishmate.repository.word.MaterialWordRepository;
import com.jihyun.englishmate.util.word.PartOfSpeechLabels;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 플래시카드 복습 범위 선택, 세션 진행, 학습 상태 갱신을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final MemberRepository memberRepository;
    private final VocabularyRepository vocabularyRepository;
    private final MaterialWordRepository materialWordRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final ReviewSessionRepository reviewSessionRepository;
    private final ReviewSessionStudyMaterialRepository reviewSessionStudyMaterialRepository;
    private final ReviewCardResultRepository reviewCardResultRepository;

    public List<ReviewScopeItemResponse> findScopeItems(Long memberId) {
        return studyMaterialRepository.findVisibleMaterialsForMember(memberId)
                .stream()
                .map(ReviewScopeItemResponse::from)
                .toList();
    }

    @Transactional
    public Long start(Long memberId, ReviewStartRequest request) {
        List<StudyMaterial> selectedMaterials = resolveSelectedMaterials(memberId, request);
        List<Long> selectedMaterialIds = selectedMaterials.stream()
                .map(StudyMaterial::getId)
                .toList();
        List<Vocabulary> targets = new ArrayList<>(vocabularyRepository.findReviewTargets(memberId, selectedMaterialIds));
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("선택한 범위에 단어장에 저장되고 의미가 입력된 단어가 없습니다.");
        }

        Collections.shuffle(targets);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        ReviewSession session = reviewSessionRepository.save(ReviewSession.start(member, targets.size()));

        reviewSessionStudyMaterialRepository.saveAll(selectedMaterials.stream()
                .map(material -> ReviewSessionStudyMaterial.create(session, material))
                .toList());

        List<ReviewCardResult> cards = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            cards.add(ReviewCardResult.create(session, targets.get(i), i + 1));
        }
        reviewCardResultRepository.saveAll(cards);
        return session.getId();
    }

    public ReviewCardResponse findCurrentCard(Long memberId, Long sessionId) {
        ReviewSession session = findSession(memberId, sessionId);
        if (session.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 복습입니다.");
        }

        ReviewCardResult card = reviewCardResultRepository
                .findByReviewSessionIdAndCardOrder(sessionId, session.getCurrentCardOrder())
                .orElseThrow(() -> new EntityNotFoundException("복습 카드를 찾을 수 없습니다."));
        List<VocabularySourceResponse> sources = findSources(memberId, List.of(card.getVocabulary().getWord().getId()))
                .getOrDefault(card.getVocabulary().getWord().getId(), List.of());
        int progressPercent = (int) Math.round((card.getCardOrder() * 100.0) / session.getTotalCards());

        return new ReviewCardResponse(
                session.getId(),
                card.getId(),
                card.getCardOrder(),
                session.getTotalCards(),
                progressPercent,
                card.getVocabulary().getWord().getNormalizedText(),
                card.getVocabulary().getMeaning(),
                PartOfSpeechLabels.labelOf(card.getVocabulary().getPartOfSpeech()),
                sources
        );
    }

    @Transactional
    public boolean answer(Long memberId, Long sessionId, ReviewAnswerRequest request) {
        ReviewSession session = findSession(memberId, sessionId);
        if (session.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 복습입니다.");
        }
        if (request.responseType() == null) {
            throw new IllegalArgumentException("복습 응답을 선택해주세요.");
        }

        ReviewCardResult card = reviewCardResultRepository
                .findByIdAndReviewSessionId(request.reviewCardResultId(), sessionId)
                .orElseThrow(() -> new EntityNotFoundException("복습 카드를 찾을 수 없습니다."));
        if (card.getCardOrder() != session.getCurrentCardOrder()) {
            throw new IllegalArgumentException("현재 순서의 카드만 응답할 수 있습니다.");
        }
        if (card.isAnswered()) {
            throw new IllegalArgumentException("이미 응답한 카드입니다.");
        }

        card.answer(request.responseType());
        LearningProgress progress = learningProgressRepository.findByVocabularyId(card.getVocabulary().getId())
                .orElseGet(() -> learningProgressRepository.save(LearningProgress.create(card.getVocabulary())));
        if (request.responseType() == ReviewResponseType.REMEMBERED) {
            progress.remember();
        } else {
            progress.markDifficult();
        }

        session.record(request.responseType());
        if (card.getCardOrder() == session.getTotalCards()) {
            session.complete();
            return true;
        }
        session.moveNext();
        return false;
    }

    public ReviewCompleteResponse findCompleteResult(Long memberId, Long sessionId) {
        ReviewSession session = findSession(memberId, sessionId);
        if (!session.isCompleted()) {
            throw new IllegalArgumentException("아직 완료되지 않은 복습입니다.");
        }

        List<ReviewCardResult> cards = reviewCardResultRepository.findAllByReviewSessionIdOrderByCardOrderAsc(sessionId);
        List<Long> wordIds = cards.stream()
                .map(card -> card.getVocabulary().getWord().getId())
                .distinct()
                .toList();
        Map<Long, List<VocabularySourceResponse>> sourcesByWordId = findSources(memberId, wordIds);

        List<ReviewCardResultResponse> cardResults = cards.stream()
                .map(card -> new ReviewCardResultResponse(
                        card.getCardOrder(),
                        card.getVocabulary().getWord().getNormalizedText(),
                        card.getVocabulary().getMeaning(),
                        card.getResponseType(),
                        card.getResponseType() == null ? "-" : card.getResponseType().getLabel(),
                        sourcesByWordId.getOrDefault(card.getVocabulary().getWord().getId(), List.of())
                ))
                .toList();

        return new ReviewCompleteResponse(
                session.getId(),
                session.getTotalCards(),
                session.getRememberedCount(),
                session.getDifficultCount(),
                reviewSessionStudyMaterialRepository.findMaterialTitlesBySessionId(sessionId),
                cardResults
        );
    }

    private List<StudyMaterial> resolveSelectedMaterials(Long memberId, ReviewStartRequest request) {
        if (request.selectAllMaterials()) {
            List<StudyMaterial> materials = studyMaterialRepository.findVisibleMaterialsForMember(memberId);
            if (materials.isEmpty()) {
                throw new IllegalArgumentException("등록된 학습 자료가 없습니다.");
            }
            return materials;
        }

        List<Long> selectedIds = request.selectedStudyMaterialIds();
        if (selectedIds.isEmpty()) {
            throw new IllegalArgumentException("학습 자료를 하나 이상 선택해주세요.");
        }

        List<StudyMaterial> materials = studyMaterialRepository.findVisibleMaterialsForMemberByIds(memberId, selectedIds);
        if (materials.size() != selectedIds.stream().distinct().count()) {
            throw new IllegalArgumentException("선택할 수 없는 학습 자료가 포함되어 있습니다.");
        }
        return materials;
    }

    private Map<Long, List<VocabularySourceResponse>> findSources(Long memberId, List<Long> wordIds) {
        if (wordIds.isEmpty()) {
            return Map.of();
        }
        List<VocabularySourceRow> sourceRows = materialWordRepository.findVocabularySourceRows(memberId, wordIds);
        return sourceRows.stream()
                .collect(Collectors.groupingBy(
                        VocabularySourceRow::wordId,
                        LinkedHashMap::new,
                        Collectors.mapping(VocabularySourceRow::toSourceResponse, Collectors.toList())
                ));
    }

    private ReviewSession findSession(Long memberId, Long sessionId) {
        return reviewSessionRepository.findByIdAndMemberId(sessionId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("복습 세션을 찾을 수 없습니다."));
    }
}
