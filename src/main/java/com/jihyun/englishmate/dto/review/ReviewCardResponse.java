package com.jihyun.englishmate.dto.review;

import com.jihyun.englishmate.dto.vocabulary.VocabularySourceResponse;
import java.util.List;

/**
 * 플래시카드 화면에 사용할 현재 카드 DTO입니다.
 */
public record ReviewCardResponse(
        Long reviewSessionId,
        Long reviewCardResultId,
        int cardOrder,
        int totalCards,
        int progressPercent,
        String wordText,
        String meaning,
        String partOfSpeechLabel,
        List<VocabularySourceResponse> sources
) {
}
