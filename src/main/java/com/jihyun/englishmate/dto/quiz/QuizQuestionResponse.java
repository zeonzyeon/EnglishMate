package com.jihyun.englishmate.dto.quiz;

import com.jihyun.englishmate.entity.quiz.QuizType;
import java.util.List;

/**
 * 퀴즈 한 문제 화면에 사용할 응답 DTO입니다.
 */
public record QuizQuestionResponse(
        Long attemptId,
        int questionOrder,
        int totalQuestions,
        int progressPercent,
        QuizType quizType,
        String quizTypeLabel,
        String questionText,
        List<String> choices,
        boolean multipleChoice,
        boolean answered,
        String submittedAnswer,
        String correctAnswer,
        Boolean correct
) {
}
