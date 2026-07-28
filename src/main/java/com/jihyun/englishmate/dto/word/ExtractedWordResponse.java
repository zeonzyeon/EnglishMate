package com.jihyun.englishmate.dto.word;

import com.jihyun.englishmate.entity.word.MaterialWord;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.util.word.PartOfSpeechLabels;

/**
 * 학습 지문에서 추출된 단어 목록 화면에 사용할 응답 DTO입니다.
 */
public record ExtractedWordResponse(
        Long wordId,
        String text,
        String normalizedText,
        String meaning,
        String partOfSpeech,
        int frequency,
        boolean savedInVocabulary
) {

    public static ExtractedWordResponse from(MaterialWord materialWord, Vocabulary vocabulary) {
        boolean savedInVocabulary = vocabulary != null;

        return new ExtractedWordResponse(
                materialWord.getWord().getId(),
                materialWord.getWord().getText(),
                materialWord.getWord().getNormalizedText(),
                savedInVocabulary ? vocabulary.getMeaning() : null,
                savedInVocabulary ? vocabulary.getPartOfSpeech() : null,
                materialWord.getFrequency(),
                savedInVocabulary
        );
    }

    public String partOfSpeechLabel() {
        return PartOfSpeechLabels.labelOf(partOfSpeech);
    }
}
