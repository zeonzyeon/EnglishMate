package com.jihyun.englishmate.repository.review;

import com.jihyun.englishmate.entity.review.ReviewCardResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
