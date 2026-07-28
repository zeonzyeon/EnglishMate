package com.jihyun.englishmate.service.vocabulary;

import com.jihyun.englishmate.dto.vocabulary.VocabularyCreateRequest;
import com.jihyun.englishmate.dto.vocabulary.VocabularyResponse;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.repository.vocabulary.VocabularyRepository;
import com.jihyun.englishmate.repository.word.WordRepository;
import com.jihyun.englishmate.util.word.EnglishStopWords;
import com.jihyun.englishmate.util.word.WordNormalizer;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 개인 단어장 조회, 저장, 삭제를 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;

    /**
     * 로그인한 회원의 단어장만 알파벳순으로 조회합니다.
     */
    public List<VocabularyResponse> findAllByMember(Long memberId) {
        return vocabularyRepository.findAllByMemberIdOrderByWord(memberId)
                .stream()
                .map(VocabularyResponse::from)
                .toList();
    }

    /**
     * 회원의 단어장에 단어를 저장합니다. 이미 있으면 저장하지 않습니다.
     */
    @Transactional
    public boolean addWord(Long memberId, Long wordId) {
        if (vocabularyRepository.existsByMemberIdAndWordId(memberId, wordId)) {
            return false;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("단어를 찾을 수 없습니다."));

        Vocabulary vocabulary = Vocabulary.createVocabulary(member, word);
        vocabularyRepository.save(vocabulary);
        return true;
    }

    /**
     * 사용자가 직접 입력한 단어를 정규화한 뒤 개인 단어장에 저장합니다.
     */
    @Transactional
    public boolean create(Long memberId, VocabularyCreateRequest request) {
        String cleanedText = WordNormalizer.clean(request.text());
        if (cleanedText.isBlank() || EnglishStopWords.contains(cleanedText)) {
            throw new IllegalArgumentException("등록할 수 있는 영어 단어를 입력해주세요.");
        }

        String normalizedText = WordNormalizer.normalize(cleanedText);
        if (normalizedText.isBlank()
                || normalizedText.length() < 2
                || EnglishStopWords.contains(normalizedText)) {
            throw new IllegalArgumentException("등록할 수 있는 영어 단어를 입력해주세요.");
        }

        Word word = wordRepository.findByNormalizedText(normalizedText)
                .orElseGet(() -> wordRepository.save(Word.createWord(cleanedText, normalizedText)));

        if (vocabularyRepository.existsByMemberIdAndWordId(memberId, word.getId())) {
            return false;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        Vocabulary vocabulary = Vocabulary.createVocabulary(member, word);
        vocabulary.updateMeaningAndPartOfSpeech(request.meaning(), request.partOfSpeech());
        vocabularyRepository.save(vocabulary);
        return true;
    }

    /**
     * 로그인한 회원 본인의 단어장 항목만 삭제합니다.
     */
    @Transactional
    public void delete(Long memberId, Long vocabularyId) {
        Vocabulary vocabulary = vocabularyRepository.findByIdAndMemberId(vocabularyId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("단어장 항목을 찾을 수 없습니다."));
        vocabularyRepository.delete(vocabulary);
    }
}
