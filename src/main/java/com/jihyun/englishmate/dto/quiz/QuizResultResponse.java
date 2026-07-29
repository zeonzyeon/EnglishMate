package com.jihyun.englishmate.dto.quiz;

import com.jihyun.englishmate.entity.quiz.QuizType;
import java.util.List;

/**
 * 퀴즈 완료 결과 화면에 사용할 DTO입니다.
 */
public record QuizResultResponse(
        Long attemptId,
        QuizType quizType,
        String quizTypeLabel,
        List<String> materialTitles,
        int totalQuestions,
        int correctCount,
        int wrongCount,
        int correctRate,
        List<QuizQuestionResultResponse> questionResults
) {
}
