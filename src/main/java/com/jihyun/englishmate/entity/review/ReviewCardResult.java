package com.jihyun.englishmate.entity.review;

import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 복습 세션의 각 플래시카드 응답 결과를 저장합니다.
 */
@Entity
@Table(
        name = "review_card_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_card_order",
                columnNames = {"review_session_id", "card_order"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewCardResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_session_id", nullable = false)
    private ReviewSession reviewSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(nullable = false)
    private int cardOrder;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ReviewResponseType responseType;

    private LocalDateTime answeredAt;

    private ReviewCardResult(ReviewSession reviewSession, Vocabulary vocabulary, int cardOrder) {
        this.reviewSession = reviewSession;
        this.vocabulary = vocabulary;
        this.cardOrder = cardOrder;
    }

    public static ReviewCardResult create(ReviewSession reviewSession, Vocabulary vocabulary, int cardOrder) {
        return new ReviewCardResult(reviewSession, vocabulary, cardOrder);
    }

    public void answer(ReviewResponseType responseType) {
        this.responseType = responseType;
        this.answeredAt = LocalDateTime.now();
    }

    public boolean isAnswered() {
        return answeredAt != null;
    }
}
