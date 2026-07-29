package com.jihyun.englishmate.dto.quiz;

import com.jihyun.englishmate.entity.material.StudyMaterial;

/**
 * 퀴즈 출제 범위 선택에 사용할 학습 자료 DTO입니다.
 */
public record QuizScopeItemResponse(
        Long id,
        String title
) {

    public static QuizScopeItemResponse from(StudyMaterial studyMaterial) {
        return new QuizScopeItemResponse(studyMaterial.getId(), studyMaterial.getTitle());
    }
}
