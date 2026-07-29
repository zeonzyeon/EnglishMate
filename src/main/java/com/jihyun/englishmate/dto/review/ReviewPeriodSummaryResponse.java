package com.jihyun.englishmate.dto.review;

import java.time.LocalDate;

/**
 * 7일 기간 전체 복습 요약 DTO입니다.
 */
public record ReviewPeriodSummaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        int totalReviewCount,
        int rememberedCount,
        int difficultCount,
        int uniqueVocabularyCount,
        int activeReviewDays,
        int rememberedRate,
        LocalDate mostReviewedDate,
        int mostReviewedCount
) {
}
