package com.jihyun.englishmate.dto.quiz;

import java.time.LocalDate;

/**
 * 하루 단위 퀴즈 학습 통계를 화면에 전달하는 DTO입니다.
 */
public record DailyQuizStatisticsResponse(
        LocalDate date,
        String dayOfWeek,
        int quizAttemptCount,
        int solvedQuestionCount,
        int correctCount,
        int wrongCount,
        int correctRate,
        int barHeightPercent,
        boolean today
) {
}
