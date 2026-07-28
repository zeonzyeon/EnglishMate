package com.jihyun.englishmate.dto.vocabulary;

import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.util.word.PartOfSpeechLabels;

/**
 * 개인 단어장 목록 화면에 사용할 응답 DTO입니다.
 */
public record VocabularyResponse(
        Long id,
        Long wordId,
        String text,
        String normalizedText,
        String meaning,
        String partOfSpeech
) {

    public static VocabularyResponse from(Vocabulary vocabulary) {
        return new VocabularyResponse(
                vocabulary.getId(),
                vocabulary.getWord().getId(),
                vocabulary.getWord().getText(),
                vocabulary.getWord().getNormalizedText(),
                vocabulary.getWord().getMeaning(),
                vocabulary.getWord().getPartOfSpeech()
        );
    }

    public String partOfSpeechLabel() {
        return PartOfSpeechLabels.labelOf(partOfSpeech);
    }
}
