package com.jihyun.englishmate.dto.material;

import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.material.StudyMaterialType;
import java.time.LocalDateTime;

/**
 * 학습 지문 응답 DTO입니다.
 */
public record StudyMaterialResponse(
        Long id,
        String title,
        String content,
        StudyMaterialType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * 엔티티를 화면 응답용 DTO로 변환합니다.
     */
    public static StudyMaterialResponse from(StudyMaterial material) {
        return new StudyMaterialResponse(
                material.getId(),
                material.getTitle(),
                material.getContent(),
                material.getType(),
                material.getCreatedAt(),
                material.getUpdatedAt()
        );
    }

    public boolean sample() {
        return type == StudyMaterialType.SAMPLE;
    }

    public boolean personal() {
        return type == StudyMaterialType.PERSONAL || type == null;
    }
}
