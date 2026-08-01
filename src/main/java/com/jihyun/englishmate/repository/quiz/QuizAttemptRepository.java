package com.jihyun.englishmate.repository.quiz;

import com.jihyun.englishmate.entity.quiz.QuizAttempt;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * QuizAttempt 데이터 접근을 담당합니다.
 */
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByIdAndMemberId(Long id, Long memberId);

    /**
     * 현재 회원의 완료된 퀴즈 응시 기록을 통계 기간 기준으로 조회합니다.
     */
    @EntityGraph(attributePaths = {"member"})
    @Query("""
            select qa
            from QuizAttempt qa
            where qa.member.id = :memberId
              and qa.completedAt is not null
              and qa.completedAt >= :startDateTime
              and qa.completedAt < :endDateTime
            order by qa.completedAt asc
            """)
    List<QuizAttempt> findCompletedAttemptsForStatistics(
            @Param("memberId") Long memberId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
