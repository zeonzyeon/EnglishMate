package com.jihyun.englishmate.dto.quiz;

import com.jihyun.englishmate.entity.quiz.QuizType;

/**
 * 퀴즈 유형 선택 화면에 표시할 DTO입니다.
 */
public record QuizTypeResponse(
        QuizType value,
        String label
) {

    public static QuizTypeResponse from(QuizType quizType) {
        return new QuizTypeResponse(quizType, quizType.getLabel());
    }
}
