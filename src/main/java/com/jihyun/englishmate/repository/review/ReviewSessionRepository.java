package com.jihyun.englishmate.repository.review;

import com.jihyun.englishmate.entity.review.ReviewSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ReviewSession 데이터 접근을 담당합니다.
 */
public interface ReviewSessionRepository extends JpaRepository<ReviewSession, Long> {

    Optional<ReviewSession> findByIdAndMemberId(Long id, Long memberId);
}
