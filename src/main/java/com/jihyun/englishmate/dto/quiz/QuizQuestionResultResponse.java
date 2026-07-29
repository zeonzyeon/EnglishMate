package com.jihyun.englishmate.dto.quiz;

/**
 * 결과 화면의 문제별 제출 결과 DTO입니다.
 */
public record QuizQuestionResultResponse(
        int questionOrder,
        String questionText,
        String submittedAnswer,
        String correctAnswer,
        Boolean correct
) {
}
