package com.jihyun.englishmate.dto.quiz;

/**
 * 답안 제출 직후 표시할 채점 결과 DTO입니다.
 */
public record QuizAnswerResponse(
        boolean correct,
        String submittedAnswer,
        String correctAnswer,
        boolean lastQuestion
) {
}
