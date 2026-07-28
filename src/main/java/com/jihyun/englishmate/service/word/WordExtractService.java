package com.jihyun.englishmate.service.word;

import com.jihyun.englishmate.dto.word.WordExtractResult;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.word.MaterialWord;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.word.MaterialWordRepository;
import com.jihyun.englishmate.repository.word.WordRepository;
import com.jihyun.englishmate.util.word.EnglishStopWords;
import com.jihyun.englishmate.util.word.WordNormalizer;
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

        Map<String, ExtractedWord> wordFrequencies = extractWords(studyMaterial.getContent());
        int savedWordCount = 0;

        for (ExtractedWord extractedWord : wordFrequencies.values()) {
            Word word = findOrCreateWord(extractedWord.text(), extractedWord.normalizedText());

            if (materialWordRepository.existsByStudyMaterialIdAndWordId(studyMaterial.getId(), word.getId())) {
                continue;
            }

            MaterialWord materialWord = MaterialWord.createMaterialWord(
                    studyMaterial,
                    word,
                    extractedWord.frequency()
            );
            materialWordRepository.save(materialWord);
            savedWordCount++;
        }

        return new WordExtractResult(wordFrequencies.size(), savedWordCount);
    }

    /**
     * 문자열에서 조건에 맞는 고유 영어 단어와 빈도를 추출합니다.
     */
    public Map<String, ExtractedWord> extractWords(String content) {
        Map<String, ExtractedWord> wordFrequencies = new LinkedHashMap<>();

        if (content == null || content.isBlank()) {
            return wordFrequencies;
        }

        for (String token : content.split("\\s+")) {
            String cleanedText = WordNormalizer.clean(token);
            String normalizedText = WordNormalizer.normalize(token);

            if (isValidWord(normalizedText)) {
                wordFrequencies.compute(
                        normalizedText,
                        (key, existing) -> existing == null
                                ? new ExtractedWord(cleanedText, normalizedText, 1)
                                : existing.increaseFrequency()
                );
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

    private Word findOrCreateWord(String text, String normalizedText) {
        return wordRepository.findByNormalizedText(normalizedText)
                .orElseGet(() -> wordRepository.save(Word.createWord(text, normalizedText)));
    }

    /**
     * 같은 normalizedText로 묶인 최초 추출 단어와 빈도를 보관합니다.
     */
    public record ExtractedWord(
            String text,
            String normalizedText,
            int frequency
    ) {

        public ExtractedWord increaseFrequency() {
            return new ExtractedWord(text, normalizedText, frequency + 1);
        }
    }
}
