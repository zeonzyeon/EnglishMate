package com.jihyun.englishmate.entity.review;

import com.jihyun.englishmate.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한 번 시작한 플래시카드 복습 진행 상태를 저장합니다.
 */
@Entity
@Table(name = "review_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int totalCards;

    @Column(nullable = false)
    private int currentCardOrder;

    @Column(nullable = false)
    private int rememberedCount;

    @Column(nullable = false)
    private int difficultCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewSessionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private ReviewSession(Member member, int totalCards) {
        this.member = member;
        this.totalCards = totalCards;
        this.currentCardOrder = 1;
        this.status = ReviewSessionStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public static ReviewSession start(Member member, int totalCards) {
        return new ReviewSession(member, totalCards);
    }

    public void record(ReviewResponseType responseType) {
        if (responseType == ReviewResponseType.REMEMBERED) {
            this.rememberedCount++;
        } else {
            this.difficultCount++;
        }
    }

    public void moveNext() {
        this.currentCardOrder++;
    }

    public void complete() {
        this.status = ReviewSessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == ReviewSessionStatus.COMPLETED;
    }
}
