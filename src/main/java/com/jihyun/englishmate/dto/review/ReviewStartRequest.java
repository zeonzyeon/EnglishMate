package com.jihyun.englishmate.dto.review;

import java.util.ArrayList;
import java.util.List;

/**
 * 플래시카드 복습 시작 요청 DTO입니다.
 */
public record ReviewStartRequest(
        boolean selectAllMaterials,
        List<Long> selectedStudyMaterialIds
) {

    public List<Long> selectedStudyMaterialIds() {
        return selectedStudyMaterialIds == null ? new ArrayList<>() : selectedStudyMaterialIds;
    }
}
