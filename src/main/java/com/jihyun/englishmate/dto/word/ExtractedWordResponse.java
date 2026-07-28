package com.jihyun.englishmate.dto.word;

import com.jihyun.englishmate.entity.word.MaterialWord;

/**
 * 학습 지문에서 추출된 단어 목록 화면에 사용할 응답 DTO입니다.
 */
public record ExtractedWordResponse(
        Long wordId,
        String text,
        String normalizedText,
        String meaning,
        String partOfSpeech,
        int frequency
) {

    public static ExtractedWordResponse from(MaterialWord materialWord) {
        return new ExtractedWordResponse(
                materialWord.getWord().getId(),
                materialWord.getWord().getText(),
                materialWord.getWord().getNormalizedText(),
                materialWord.getWord().getMeaning(),
                materialWord.getWord().getPartOfSpeech(),
                materialWord.getFrequency()
        );
    }
}
