package com.jihyun.englishmate.dto.word;

/**
 * 단어 추출 결과를 화면에 전달합니다.
 */
public record WordExtractResult(
        int extractedWordCount,
        int savedWordCount
) {
}
