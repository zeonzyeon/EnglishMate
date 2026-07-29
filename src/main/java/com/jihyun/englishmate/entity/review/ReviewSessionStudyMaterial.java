package com.jihyun.englishmate.entity.review;

import com.jihyun.englishmate.entity.material.StudyMaterial;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 복습 세션에서 선택한 학습 자료 범위를 저장합니다.
 */
@Entity
@Table(
        name = "review_session_study_materials",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_session_material",
                columnNames = {"review_session_id", "study_material_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSessionStudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_session_id", nullable = false)
    private ReviewSession reviewSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_material_id", nullable = false)
    private StudyMaterial studyMaterial;

    private ReviewSessionStudyMaterial(ReviewSession reviewSession, StudyMaterial studyMaterial) {
        this.reviewSession = reviewSession;
        this.studyMaterial = studyMaterial;
    }

    public static ReviewSessionStudyMaterial create(ReviewSession reviewSession, StudyMaterial studyMaterial) {
        return new ReviewSessionStudyMaterial(reviewSession, studyMaterial);
    }
}
