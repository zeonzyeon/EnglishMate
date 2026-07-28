package com.jihyun.englishmate.dto.word;

import jakarta.validation.constraints.Size;

/**
 * Word 의미와 품사 수정 요청 DTO입니다.
 */
public record WordUpdateRequest(
        @Size(max = 255, message = "의미는 255자 이하로 입력해주세요.")
        String meaning,

        @Size(max = 50, message = "품사는 50자 이하로 선택해주세요.")
        String partOfSpeech
) {
}
