package com.jihyun.englishmate.repository.quiz;

import com.jihyun.englishmate.entity.quiz.QuizAttempt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * QuizAttempt 데이터 접근을 담당합니다.
 */
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByIdAndMemberId(Long id, Long memberId);
}
