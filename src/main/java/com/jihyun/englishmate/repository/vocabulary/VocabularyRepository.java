package com.jihyun.englishmate.repository.vocabulary;

import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Vocabulary 엔티티의 데이터 접근을 담당합니다.
 */
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    boolean existsByMemberIdAndWordId(Long memberId, Long wordId);

    Optional<Vocabulary> findByIdAndMemberId(Long id, Long memberId);

    Optional<Vocabulary> findByMemberIdAndWordId(Long memberId, Long wordId);

    /**
     * 회원의 단어장을 단어 알파벳순으로 조회합니다.
     */
    @Query("""
            select v
            from Vocabulary v
            join fetch v.word w
            where v.member.id = :memberId
            order by w.normalizedText asc
            """)
    List<Vocabulary> findAllByMemberIdOrderByWord(@Param("memberId") Long memberId);

    /**
     * 추출 단어 목록에 표시할 회원별 단어장 정보를 한 번에 조회합니다.
     */
    @Query("""
            select v
            from Vocabulary v
            join fetch v.word w
            where v.member.id = :memberId
              and w.id in :wordIds
            """)
    List<Vocabulary> findAllByMemberIdAndWordIdIn(
            @Param("memberId") Long memberId,
            @Param("wordIds") List<Long> wordIds
    );
}
