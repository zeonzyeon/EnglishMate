package com.jihyun.englishmate.service.word;

import com.jihyun.englishmate.dto.word.WordExtractResult;
import com.jihyun.englishmate.dto.word.ExtractedWordResponse;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.entity.word.MaterialWord;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.vocabulary.VocabularyRepository;
import com.jihyun.englishmate.repository.word.MaterialWordRepository;
import com.jihyun.englishmate.repository.word.WordRepository;
import com.jihyun.englishmate.util.word.EnglishStopWords;
import com.jihyun.englishmate.util.word.WordNormalizer;
import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final VocabularyRepository vocabularyRepository;

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

            if (cleanedText.isBlank() || EnglishStopWords.contains(cleanedText)) {
                continue;
            }

            String normalizedText = WordNormalizer.normalize(cleanedText);

            if (isValidNormalizedWord(normalizedText)) {
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

    /**
     * 본인 소유의 학습 지문에서 추출된 단어 목록을 조회합니다.
     */
    public List<ExtractedWordResponse> findExtractedWords(Long memberId, Long studyMaterialId) {
        studyMaterialRepository.findByIdAndMemberId(studyMaterialId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("학습 자료를 찾을 수 없습니다."));

        List<MaterialWord> materialWords = materialWordRepository.findAllByStudyMaterialIdOrderByWord(studyMaterialId);
        List<Long> wordIds = materialWords.stream()
                .map(materialWord -> materialWord.getWord().getId())
                .toList();
        if (wordIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Vocabulary> vocabularyByWordId = vocabularyRepository.findAllByMemberIdAndWordIdIn(memberId, wordIds)
                .stream()
                .collect(Collectors.toMap(vocabulary -> vocabulary.getWord().getId(), Function.identity()));

        return materialWords.stream()
                .map(materialWord -> ExtractedWordResponse.from(
                        materialWord,
                        vocabularyByWordId.get(materialWord.getWord().getId())
                ))
                .toList();
    }

    private boolean isValidNormalizedWord(String normalizedText) {
        return normalizedText != null
                && !normalizedText.isBlank()
                && normalizedText.length() >= 2
                && !EnglishStopWords.contains(normalizedText);
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
