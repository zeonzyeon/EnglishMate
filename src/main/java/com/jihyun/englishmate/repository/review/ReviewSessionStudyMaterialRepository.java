package com.jihyun.englishmate.repository.review;

import com.jihyun.englishmate.entity.review.ReviewSessionStudyMaterial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 복습 세션 범위 데이터 접근을 담당합니다.
 */
public interface ReviewSessionStudyMaterialRepository extends JpaRepository<ReviewSessionStudyMaterial, Long> {

    @Query("""
            select rssm.studyMaterial.title
            from ReviewSessionStudyMaterial rssm
            where rssm.reviewSession.id = :sessionId
            order by rssm.studyMaterial.title asc
            """)
    List<String> findMaterialTitlesBySessionId(@Param("sessionId") Long sessionId);
}
