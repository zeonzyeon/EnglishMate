package com.jihyun.englishmate.repository.word;

import com.jihyun.englishmate.entity.word.MaterialWord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 학습 지문과 추출 단어의 연결 정보를 관리합니다.
 */
public interface MaterialWordRepository extends JpaRepository<MaterialWord, Long> {

    boolean existsByStudyMaterialIdAndWordId(Long studyMaterialId, Long wordId);

    /**
     * 학습 지문에서 추출된 단어 목록을 알파벳순으로 조회합니다.
     */
    @Query("""
            select mw
            from MaterialWord mw
            join fetch mw.word w
            where mw.studyMaterial.id = :studyMaterialId
            order by w.normalizedText asc
            """)
    List<MaterialWord> findAllByStudyMaterialIdOrderByWord(@Param("studyMaterialId") Long studyMaterialId);
}
