package com.jihyun.englishmate.dto.review;

import java.time.LocalDate;

/**
 * 하루 단위 플래시카드 복습 통계 DTO입니다.
 */
public record DailyReviewStatisticsResponse(
        LocalDate date,
        String dayOfWeek,
        int totalReviewCount,
        int rememberedCount,
        int difficultCount,
        int uniqueVocabularyCount,
        int rememberedRate,
        int barHeightPercent,
        boolean today
) {
}
