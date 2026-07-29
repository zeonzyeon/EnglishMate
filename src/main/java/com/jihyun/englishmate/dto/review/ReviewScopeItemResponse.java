package com.jihyun.englishmate.dto.review;

import com.jihyun.englishmate.entity.material.StudyMaterial;

/**
 * 복습 범위 선택에 사용할 학습 자료 DTO입니다.
 */
public record ReviewScopeItemResponse(
        Long id,
        String title
) {

    public static ReviewScopeItemResponse from(StudyMaterial studyMaterial) {
        return new ReviewScopeItemResponse(studyMaterial.getId(), studyMaterial.getTitle());
    }
}
