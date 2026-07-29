package com.jihyun.englishmate.dto.quiz;

import com.jihyun.englishmate.entity.quiz.QuizType;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 퀴즈 시작 요청 DTO입니다.
 */
public record QuizStartRequest(
        @NotNull(message = "퀴즈 유형을 선택해주세요.")
        QuizType quizType,

        boolean selectAllMaterials,

        List<Long> selectedStudyMaterialIds
) {

    public List<Long> selectedStudyMaterialIds() {
        return selectedStudyMaterialIds == null ? new ArrayList<>() : selectedStudyMaterialIds;
    }
}
