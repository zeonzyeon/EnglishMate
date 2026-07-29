package com.jihyun.englishmate.dto.review;

import com.jihyun.englishmate.entity.review.ReviewResponseType;
import jakarta.validation.constraints.NotNull;

/**
 * 플래시카드 응답 요청 DTO입니다.
 */
public record ReviewAnswerRequest(
        Long reviewCardResultId,

        @NotNull(message = "복습 응답을 선택해주세요.")
        ReviewResponseType responseType
) {
}
