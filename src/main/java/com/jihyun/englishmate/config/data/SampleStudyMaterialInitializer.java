package com.jihyun.englishmate.config.data;

import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.material.StudyMaterialType;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.repository.word.MaterialWordRepository;
import com.jihyun.englishmate.service.word.WordExtractService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Guest Mode에서 보여줄 SAMPLE 학습 자료를 최초 1회 생성합니다.
 */
@Component
@RequiredArgsConstructor
public class SampleStudyMaterialInitializer implements CommandLineRunner {

    private static final String SAMPLE_OWNER_EMAIL = "sample@englishmate.local";
    private static final String SAMPLE_TITLE = "EnglishMate Sample: A Better Study Routine";
    private static final String SAMPLE_CONTENT = """
            Many students want to improve their English, but they often study without a clear routine.
            A better routine starts with short reading practice. Read one paragraph, choose important words,
            and write your own example sentences. After that, review the words again and answer a few quiz questions.
            Small daily practice helps learners remember vocabulary and understand English texts with more confidence.
            """;

    private final MemberRepository memberRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final MaterialWordRepository materialWordRepository;
    private final WordExtractService wordExtractService;

    @Override
    @Transactional
    public void run(String... args) {
        if (studyMaterialRepository.existsByType(StudyMaterialType.SAMPLE)) {
            return;
        }

        Member sampleOwner = findOrCreateSampleOwner();
        StudyMaterial sample = studyMaterialRepository.save(
                StudyMaterial.createSample(sampleOwner, SAMPLE_TITLE, SAMPLE_CONTENT)
        );
        wordExtractService.reextractMaterialWords(sample);
        applySampleMeanings(sample);
    }

    /**
     * 기존 DB의 member_id NOT NULL 제약과 충돌하지 않도록 SAMPLE 전용 내부 회원을 사용합니다.
     */
    private Member findOrCreateSampleOwner() {
        return memberRepository.findByEmail(SAMPLE_OWNER_EMAIL)
                .orElseGet(() -> memberRepository.save(
                        Member.createMember(SAMPLE_OWNER_EMAIL, "{sample-owner}", "EnglishMate Sample")
                ));
    }

    /**
     * 비회원도 단어 의미를 확인할 수 있도록 SAMPLE 단어에 기본 의미와 품사를 채웁니다.
     */
    private void applySampleMeanings(StudyMaterial sample) {
        Map<String, SampleWordMeta> metaByWord = Map.ofEntries(
                Map.entry("student", new SampleWordMeta("학생", "Noun")),
                Map.entry("improve", new SampleWordMeta("향상시키다", "Verb")),
                Map.entry("english", new SampleWordMeta("영어", "Noun")),
                Map.entry("study", new SampleWordMeta("공부하다", "Verb")),
                Map.entry("clear", new SampleWordMeta("명확한", "Adjective")),
                Map.entry("routine", new SampleWordMeta("학습 루틴", "Noun")),
                Map.entry("better", new SampleWordMeta("더 나은", "Adjective")),
                Map.entry("start", new SampleWordMeta("시작하다", "Verb")),
                Map.entry("short", new SampleWordMeta("짧은", "Adjective")),
                Map.entry("reading", new SampleWordMeta("읽기", "Noun")),
                Map.entry("practice", new SampleWordMeta("연습", "Noun")),
                Map.entry("paragraph", new SampleWordMeta("문단", "Noun")),
                Map.entry("choose", new SampleWordMeta("선택하다", "Verb")),
                Map.entry("important", new SampleWordMeta("중요한", "Adjective")),
                Map.entry("word", new SampleWordMeta("단어", "Noun")),
                Map.entry("write", new SampleWordMeta("쓰다", "Verb")),
                Map.entry("example", new SampleWordMeta("예시", "Noun")),
                Map.entry("sentence", new SampleWordMeta("문장", "Noun")),
                Map.entry("review", new SampleWordMeta("복습하다", "Verb")),
                Map.entry("answer", new SampleWordMeta("답하다", "Verb")),
                Map.entry("quiz", new SampleWordMeta("퀴즈", "Noun")),
                Map.entry("question", new SampleWordMeta("문제", "Noun")),
                Map.entry("daily", new SampleWordMeta("매일의", "Adjective")),
                Map.entry("learner", new SampleWordMeta("학습자", "Noun")),
                Map.entry("remember", new SampleWordMeta("기억하다", "Verb")),
                Map.entry("vocabulary", new SampleWordMeta("어휘", "Noun")),
                Map.entry("understand", new SampleWordMeta("이해하다", "Verb")),
                Map.entry("text", new SampleWordMeta("글", "Noun")),
                Map.entry("confidence", new SampleWordMeta("자신감", "Noun"))
        );

        materialWordRepository.findAllByStudyMaterialIdOrderByWord(sample.getId())
                .forEach(materialWord -> {
                    SampleWordMeta meta = metaByWord.get(materialWord.getWord().getNormalizedText());
                    if (meta != null) {
                        materialWord.updateMeaningAndPartOfSpeech(meta.meaning(), meta.partOfSpeech());
                    }
                });
    }

    private record SampleWordMeta(String meaning, String partOfSpeech) {
    }
}
