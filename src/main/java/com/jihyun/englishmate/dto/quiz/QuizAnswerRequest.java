package com.jihyun.englishmate.dto.quiz;

import jakarta.validation.constraints.NotBlank;

/**
 * 퀴즈 답안 제출 요청 DTO입니다.
 */
public record QuizAnswerRequest(
        @NotBlank(message = "답안을 입력하거나 선택해주세요.")
        String submittedAnswer
) {
}
