package com.jihyun.englishmate.repository.review;

import com.jihyun.englishmate.entity.review.LearningProgress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

/**
 * LearningProgress 데이터 접근을 담당합니다.
 */
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    Optional<LearningProgress> findByVocabularyId(Long vocabularyId);

    @Modifying
    void deleteByVocabularyId(Long vocabularyId);
}
