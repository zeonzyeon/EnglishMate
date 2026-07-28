package com.jihyun.englishmate.service.word;

import com.jihyun.englishmate.dto.word.WordUpdateRequest;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.word.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Word 마스터 데이터 조회와 의미/품사 수정을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordService {

    private final WordRepository wordRepository;

    /**
     * 수정 화면에 표시할 Word를 조회합니다.
     */
    public Word findById(Long wordId) {
        return wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("단어를 찾을 수 없습니다."));
    }

    /**
     * Word의 의미와 품사만 수정합니다.
     */
    @Transactional
    public void update(Long wordId, WordUpdateRequest request) {
        Word word = findById(wordId);
        word.updateMeaningAndPartOfSpeech(request.meaning(), request.partOfSpeech());
    }
}
