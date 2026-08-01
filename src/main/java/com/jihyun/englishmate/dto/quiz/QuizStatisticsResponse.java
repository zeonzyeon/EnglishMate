package com.jihyun.englishmate.dto.quiz;

import java.time.LocalDate;
import java.util.List;

/**
 * My Page에서 사용하는 주간 퀴즈 통계 DTO입니다.
 */
public record QuizStatisticsResponse(
        LocalDate startDate,
        LocalDate endDate,
        LocalDate previousEndDate,
        LocalDate nextEndDate,
        boolean canMoveNext,
        List<DailyQuizStatisticsResponse> dailyStatistics,
        QuizPeriodSummaryResponse summary
) {
}
