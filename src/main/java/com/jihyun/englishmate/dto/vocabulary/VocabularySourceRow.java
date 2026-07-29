package com.jihyun.englishmate.dto.vocabulary;

/**
 * Word ID와 학습 자료 출처를 함께 조회하기 위한 내부 조회 DTO입니다.
 */
public record VocabularySourceRow(
        Long wordId,
        Long studyMaterialId,
        String title
) {

    public VocabularySourceResponse toSourceResponse() {
        return new VocabularySourceResponse(studyMaterialId, title);
    }
}
