package com.jihyun.englishmate.dto.vocabulary;

/**
 * 개인 단어장 단어가 포함된 학습 자료 출처 DTO입니다.
 */
public record VocabularySourceResponse(
        Long studyMaterialId,
        String title
) {
}
