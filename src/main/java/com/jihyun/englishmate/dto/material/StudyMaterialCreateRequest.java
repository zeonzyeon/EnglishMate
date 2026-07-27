package com.jihyun.englishmate.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 학습 지문 등록 요청 DTO입니다.
 */
public record StudyMaterialCreateRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
        String title,

        @NotBlank(message = "영어 지문을 입력해주세요.")
        @Size(max = 10000, message = "내용은 10000자 이하로 입력해주세요.")
        String content
) {
}
