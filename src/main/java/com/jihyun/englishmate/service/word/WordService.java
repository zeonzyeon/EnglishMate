package com.jihyun.englishmate.service.word;

import com.jihyun.englishmate.dto.word.WordUpdateRequest;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.repository.vocabulary.VocabularyRepository;
import com.jihyun.englishmate.repository.word.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Word 조회와 회원별 단어장 의미/품사 수정을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordService {

    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;
    private final VocabularyRepository vocabularyRepository;

    /**
     * 수정 화면에 표시할 공통 Word를 조회합니다.
     */
    public Word findById(Long wordId) {
        return wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("단어를 찾을 수 없습니다."));
    }

    /**
     * 회원 개인 단어장에 저장된 의미/품사를 수정 폼 값으로 조회합니다.
     */
    public WordUpdateRequest findUpdateRequest(Long memberId, Long wordId) {
        return vocabularyRepository.findByMemberIdAndWordId(memberId, wordId)
                .map(vocabulary -> new WordUpdateRequest(vocabulary.getWord().getNormalizedText(), vocabulary.getMeaning(), vocabulary.getPartOfSpeech()))
                .orElseGet(() -> new WordUpdateRequest("","", ""));
    }

    /**
     * 회원 개인 단어장에 기본형/의미/품사를 저장합니다. 단어장 항목이 없으면 먼저 생성합니다.
     */
    @Transactional
    public void update(Long memberId, Long wordId, WordUpdateRequest request) {
        Vocabulary vocabulary = vocabularyRepository.findByMemberIdAndWordId(memberId, wordId)
                .orElseGet(() -> createVocabulary(memberId, wordId));

        vocabulary.updateMeaningAndPartOfSpeech(
                request.meaning(),
                request.partOfSpeech()
        );

        vocabulary.getWord().updateNormalizedText(
                request.normalizedText()
        );
    }

    private Vocabulary createVocabulary(Long memberId, Long wordId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        Word word = findById(wordId);
        return vocabularyRepository.save(Vocabulary.createVocabulary(member, word));
    }
}
