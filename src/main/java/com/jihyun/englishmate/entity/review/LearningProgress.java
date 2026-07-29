package com.jihyun.englishmate.entity.review;

import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 단어장 항목의 복습 누적 상태를 관리합니다.
 */
@Entity
@Table(name = "learning_progresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false, unique = true)
    private Vocabulary vocabulary;

    @Column(nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private int rememberedCount;

    @Column(nullable = false)
    private int difficultCount;

    @Column(nullable = false)
    private int consecutiveRememberedCount;

    @Column(nullable = false)
    private boolean mastered;

    private LocalDateTime lastReviewedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LearningProgress(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        this.mastered = false;
    }

    public static LearningProgress create(Vocabulary vocabulary) {
        return new LearningProgress(vocabulary);
    }

    public void remember() {
        this.reviewCount++;
        this.rememberedCount++;
        this.consecutiveRememberedCount++;
        this.lastReviewedAt = LocalDateTime.now();
    }

    public void markDifficult() {
        this.reviewCount++;
        this.difficultCount++;
        this.consecutiveRememberedCount = 0;
        this.lastReviewedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
