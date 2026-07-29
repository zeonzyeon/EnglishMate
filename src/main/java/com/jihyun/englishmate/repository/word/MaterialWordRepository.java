package com.jihyun.englishmate.repository.word;

import com.jihyun.englishmate.entity.word.MaterialWord;
import com.jihyun.englishmate.dto.vocabulary.VocabularySourceRow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 학습 지문과 추출 단어의 연결 정보를 관리합니다.
 */
public interface MaterialWordRepository extends JpaRepository<MaterialWord, Long> {

    boolean existsByStudyMaterialIdAndWordId(Long studyMaterialId, Long wordId);

    /**
     * 학습 자료에 연결된 기존 단어 연결을 모두 삭제합니다. Word 자체는 삭제하지 않습니다.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
            delete from MaterialWord mw
            where mw.studyMaterial.id = :studyMaterialId
            """)
    void deleteAllByStudyMaterialId(@Param("studyMaterialId") Long studyMaterialId);

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

    /**
     * 회원 단어장에 표시할 단어별 학습 자료 출처를 한 번에 조회합니다.
     */
    @Query("""
            select distinct new com.jihyun.englishmate.dto.vocabulary.VocabularySourceRow(
                w.id,
                sm.id,
                sm.title
            )
            from MaterialWord mw
            join mw.word w
            join mw.studyMaterial sm
            where sm.member.id = :memberId
              and w.id in :wordIds
            order by sm.title asc
            """)
    List<VocabularySourceRow> findVocabularySourceRows(
            @Param("memberId") Long memberId,
            @Param("wordIds") List<Long> wordIds
    );
}
