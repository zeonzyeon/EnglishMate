package com.jihyun.englishmate.repository.word;

import com.jihyun.englishmate.entity.word.Word;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Word 마스터 엔티티의 데이터 접근을 담당합니다.
 */
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByNormalizedText(String normalizedText);

    boolean existsByNormalizedText(String normalizedText);
}
