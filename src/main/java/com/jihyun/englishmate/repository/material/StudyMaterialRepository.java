package com.jihyun.englishmate.repository.material;

import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.material.StudyMaterialType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * StudyMaterial 엔티티의 데이터 접근을 담당합니다.
 */
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {

    /**
     * 로그인한 회원의 학습 지문 목록만 조회합니다.
     */
    List<StudyMaterial> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<StudyMaterial> findAllByMemberIdAndIdIn(Long memberId, Collection<Long> ids);

    /**
     * 상세 조회와 권한 검증을 함께 처리하기 위해 회원 ID 조건을 포함합니다.
     */
    Optional<StudyMaterial> findByIdAndMemberId(Long id, Long memberId);

    boolean existsByType(StudyMaterialType type);

    /**
     * Guest에게는 SAMPLE 학습 자료만 보여줍니다.
     */
    List<StudyMaterial> findAllByTypeOrderByCreatedAtDesc(StudyMaterialType type);

    /**
     * 회원에게 보이는 SAMPLE과 본인 PERSONAL 학습 자료를 함께 조회합니다.
     */
    @Query("""
            select sm
            from StudyMaterial sm
            where (
                    sm.type = com.jihyun.englishmate.entity.material.StudyMaterialType.SAMPLE
                    and not exists (
                        select 1
                        from MemberSampleHide hide
                        where hide.member.id = :memberId
                          and hide.studyMaterial.id = sm.id
                    )
                  )
               or (
                    (sm.type = com.jihyun.englishmate.entity.material.StudyMaterialType.PERSONAL or sm.type is null)
                    and sm.member.id = :memberId
                  )
            order by sm.type asc, sm.createdAt desc
            """)
    List<StudyMaterial> findVisibleMaterialsForMember(@Param("memberId") Long memberId);

    /**
     * 회원이 선택 가능한 학습 자료 ID 목록을 조회합니다.
     */
    @Query("""
            select sm
            from StudyMaterial sm
            where sm.id in :ids
              and (
                    (
                        sm.type = com.jihyun.englishmate.entity.material.StudyMaterialType.SAMPLE
                        and not exists (
                            select 1
                            from MemberSampleHide hide
                            where hide.member.id = :memberId
                              and hide.studyMaterial.id = sm.id
                        )
                    )
                    or (
                        (sm.type = com.jihyun.englishmate.entity.material.StudyMaterialType.PERSONAL or sm.type is null)
                        and sm.member.id = :memberId
                    )
                  )
            """)
    List<StudyMaterial> findVisibleMaterialsForMemberByIds(
            @Param("memberId") Long memberId,
            @Param("ids") Collection<Long> ids
    );

    /**
     * Guest가 상세 조회할 수 있는 SAMPLE 자료를 조회합니다.
     */
    Optional<StudyMaterial> findByIdAndType(Long id, StudyMaterialType type);

    /**
     * 회원이 조회할 수 있는 SAMPLE 또는 본인 PERSONAL 자료를 조회합니다.
     */
    @Query("""
            select sm
            from StudyMaterial sm
            where sm.id = :id
              and (
                    (
                        sm.type = com.jihyun.englishmate.entity.material.StudyMaterialType.SAMPLE
                        and not exists (
                            select 1
                            from MemberSampleHide hide
                            where hide.member.id = :memberId
                              and hide.studyMaterial.id = sm.id
                        )
                    )
                    or (
                        (sm.type = com.jihyun.englishmate.entity.material.StudyMaterialType.PERSONAL or sm.type is null)
                        and sm.member.id = :memberId
                    )
                  )
            """)
    Optional<StudyMaterial> findVisibleMaterialForMember(
            @Param("memberId") Long memberId,
            @Param("id") Long id
    );
}
