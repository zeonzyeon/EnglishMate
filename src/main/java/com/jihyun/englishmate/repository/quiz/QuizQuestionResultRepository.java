package com.jihyun.englishmate.repository.quiz;

import com.jihyun.englishmate.entity.quiz.QuizQuestionResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 퀴즈 문제 결과 데이터 접근을 담당합니다.
 */
public interface QuizQuestionResultRepository extends JpaRepository<QuizQuestionResult, Long> {

    @EntityGraph(attributePaths = {"word", "choices"})
    Optional<QuizQuestionResult> findByQuizAttemptIdAndQuestionOrder(Long quizAttemptId, int questionOrder);

    @EntityGraph(attributePaths = {"word", "choices"})
    List<QuizQuestionResult> findAllByQuizAttemptIdOrderByQuestionOrderAsc(Long quizAttemptId);

    boolean existsByQuizAttemptIdAndQuestionOrderAndAnsweredAtIsNotNull(Long quizAttemptId, int questionOrder);

    int countByQuizAttemptIdAndAnsweredAtIsNotNull(Long quizAttemptId);
}
