package com.jihyun.englishmate.repository.quiz;

import com.jihyun.englishmate.entity.quiz.QuizAttemptStudyMaterial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 퀴즈 출제 범위 데이터 접근을 담당합니다.
 */
public interface QuizAttemptStudyMaterialRepository extends JpaRepository<QuizAttemptStudyMaterial, Long> {

    @Query("""
            select qasm.studyMaterial.title
            from QuizAttemptStudyMaterial qasm
            where qasm.quizAttempt.id = :attemptId
            order by qasm.studyMaterial.title asc
            """)
    List<String> findMaterialTitlesByAttemptId(@Param("attemptId") Long attemptId);
}
