package com.jihyun.englishmate.dto.vocabulary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 개인 단어장에 단어를 직접 등록할 때 사용하는 요청 DTO입니다.
 */
public record VocabularyCreateRequest(
        @NotBlank(message = "단어를 입력해주세요.")
        @Size(max = 100, message = "단어는 100자 이하로 입력해주세요.")
        String text,

        @Size(max = 255, message = "의미는 255자 이하로 입력해주세요.")
        String meaning,

        @Size(max = 50, message = "품사는 50자 이하로 선택해주세요.")
        String partOfSpeech
) {
}
