package com.jihyun.englishmate.dto.review;

import java.time.LocalDate;
import java.util.List;

/**
 * Review 페이지 상단의 주간 복습 통계 DTO입니다.
 */
public record ReviewStatisticsResponse(
        LocalDate startDate,
        LocalDate endDate,
        LocalDate previousEndDate,
        LocalDate nextEndDate,
        boolean canMoveNext,
        List<DailyReviewStatisticsResponse> dailyStatistics,
        ReviewPeriodSummaryResponse summary
) {
}
