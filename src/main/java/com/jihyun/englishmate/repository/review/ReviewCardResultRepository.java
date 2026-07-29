package com.jihyun.englishmate.repository.review;

import com.jihyun.englishmate.entity.review.ReviewCardResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ReviewCardResult 데이터 접근을 담당합니다.
 */
public interface ReviewCardResultRepository extends JpaRepository<ReviewCardResult, Long> {

    @EntityGraph(attributePaths = {"vocabulary", "vocabulary.word"})
    Optional<ReviewCardResult> findByReviewSessionIdAndCardOrder(Long reviewSessionId, int cardOrder);

    @EntityGraph(attributePaths = {"vocabulary", "vocabulary.word"})
    Optional<ReviewCardResult> findByIdAndReviewSessionId(Long id, Long reviewSessionId);

    @EntityGraph(attributePaths = {"vocabulary", "vocabulary.word"})
    List<ReviewCardResult> findAllByReviewSessionIdOrderByCardOrderAsc(Long reviewSessionId);

    /**
     * 현재 회원의 응답 완료 복습 기록을 기간 단위로 한 번에 조회합니다.
     */
    @EntityGraph(attributePaths = {"vocabulary", "vocabulary.word", "reviewSession"})
    @Query("""
            select rcr
            from ReviewCardResult rcr
            join rcr.reviewSession rs
            where rs.member.id = :memberId
              and rcr.responseType is not null
              and rcr.answeredAt >= :startDateTime
              and rcr.answeredAt < :endDateTime
            order by rcr.answeredAt asc
            """)
    List<ReviewCardResult> findAnsweredCardsForStatistics(
            @Param("memberId") Long memberId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
