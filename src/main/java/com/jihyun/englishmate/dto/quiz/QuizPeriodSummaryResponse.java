package com.jihyun.englishmate.dto.quiz;

import java.time.LocalDate;

/**
 * 7일 기간의 퀴즈 통계 요약 DTO입니다.
 */
public record QuizPeriodSummaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        int quizAttemptCount,
        int solvedQuestionCount,
        int correctCount,
        int wrongCount,
        int averageCorrectRate
) {
}
