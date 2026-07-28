package com.jihyun.englishmate.service.word;

import com.jihyun.englishmate.dto.word.WordExtractResult;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.word.MaterialWord;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.word.MaterialWordRepository;
import com.jihyun.englishmate.repository.word.WordRepository;
import com.jihyun.englishmate.util.word.EnglishStopWords;
import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 학습 지문에서 단어를 추출하고 DB에 저장합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordExtractService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final WordRepository wordRepository;
    private final MaterialWordRepository materialWordRepository;

    /**
     * 본인 소유의 학습 지문에서 단어를 추출하고 저장합니다.
     */
    @Transactional
    public WordExtractResult extractAndSave(Long memberId, Long studyMaterialId) {
        StudyMaterial studyMaterial = studyMaterialRepository.findByIdAndMemberId(studyMaterialId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("학습 자료를 찾을 수 없습니다."));

        Map<String, Integer> wordFrequencies = extractWords(studyMaterial.getContent());
        int savedWordCount = 0;

        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            Word word = findOrCreateWord(entry.getKey());

            if (materialWordRepository.existsByStudyMaterialIdAndWordId(studyMaterial.getId(), word.getId())) {
                continue;
            }

            MaterialWord materialWord = MaterialWord.createMaterialWord(studyMaterial, word, entry.getValue());
            materialWordRepository.save(materialWord);
            savedWordCount++;
        }

        return new WordExtractResult(wordFrequencies.size(), savedWordCount);
    }

    /**
     * 문자열에서 조건에 맞는 고유 영어 단어와 빈도를 추출합니다.
     */
    public Map<String, Integer> extractWords(String content) {
        Map<String, Integer> wordFrequencies = new LinkedHashMap<>();

        if (content == null || content.isBlank()) {
            return wordFrequencies;
        }

        String normalizedContent = content.toLowerCase()
                .replaceAll("[^a-z\\s]", " ");

        for (String token : normalizedContent.split("\\s+")) {
            if (isValidWord(token)) {
                wordFrequencies.merge(token, 1, Integer::sum);
            }
        }

        return wordFrequencies;
    }

    private boolean isValidWord(String token) {
        return token != null
                && !token.isBlank()
                && token.length() >= 2
                && !EnglishStopWords.contains(token);
    }

    private Word findOrCreateWord(String normalizedText) {
        return wordRepository.findByNormalizedText(normalizedText)
                .orElseGet(() -> wordRepository.save(Word.createWord(normalizedText)));
    }
}
