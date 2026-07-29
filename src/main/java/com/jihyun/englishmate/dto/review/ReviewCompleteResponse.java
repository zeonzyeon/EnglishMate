package com.jihyun.englishmate.dto.review;

import java.util.List;

/**
 * 플래시카드 복습 완료 화면 DTO입니다.
 */
public record ReviewCompleteResponse(
        Long reviewSessionId,
        int totalCards,
        int rememberedCount,
        int difficultCount,
        List<String> materialTitles,
        List<ReviewCardResultResponse> cardResults
) {
}
