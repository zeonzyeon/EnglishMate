package com.jihyun.englishmate.entity.quiz;

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
 * 퀴즈 출제 범위로 선택한 학습 자료를 추적합니다.
 */
@Entity
@Table(
        name = "quiz_attempt_study_materials",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quiz_attempt_material",
                columnNames = {"quiz_attempt_id", "study_material_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizAttemptStudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_material_id", nullable = false)
    private StudyMaterial studyMaterial;

    private QuizAttemptStudyMaterial(QuizAttempt quizAttempt, StudyMaterial studyMaterial) {
        this.quizAttempt = quizAttempt;
        this.studyMaterial = studyMaterial;
    }

    public static QuizAttemptStudyMaterial create(QuizAttempt quizAttempt, StudyMaterial studyMaterial) {
        return new QuizAttemptStudyMaterial(quizAttempt, studyMaterial);
    }
}
