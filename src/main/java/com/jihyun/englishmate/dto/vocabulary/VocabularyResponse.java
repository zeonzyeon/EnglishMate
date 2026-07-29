package com.jihyun.englishmate.dto.vocabulary;

import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.util.word.PartOfSpeechLabels;
import java.util.List;

/**
 * 개인 단어장 목록 화면에 사용할 응답 DTO입니다.
 */
public record VocabularyResponse(
        Long id,
        Long wordId,
        String text,
        String normalizedText,
        String meaning,
        String partOfSpeech,
        List<VocabularySourceResponse> sourceMaterials
) {

    public static VocabularyResponse from(Vocabulary vocabulary) {
        return from(vocabulary, List.of());
    }

    public static VocabularyResponse from(
            Vocabulary vocabulary,
            List<VocabularySourceResponse> sourceMaterials
    ) {
        return new VocabularyResponse(
                vocabulary.getId(),
                vocabulary.getWord().getId(),
                vocabulary.getWord().getText(),
                vocabulary.getWord().getNormalizedText(),
                vocabulary.getMeaning(),
                vocabulary.getPartOfSpeech(),
                sourceMaterials
        );
    }

    public String partOfSpeechLabel() {
        return PartOfSpeechLabels.labelOf(partOfSpeech);
    }
}
