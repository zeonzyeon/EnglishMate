package com.jihyun.englishmate.repository.material;

import com.jihyun.englishmate.entity.material.StudyMaterial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * StudyMaterial 엔티티의 데이터 접근을 담당합니다.
 */
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {

    /**
     * 로그인한 회원의 학습 지문 목록만 조회합니다.
     */
    List<StudyMaterial> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    /**
     * 상세 조회와 권한 검증을 함께 처리하기 위해 회원 ID 조건을 포함합니다.
     */
    Optional<StudyMaterial> findByIdAndMemberId(Long id, Long memberId);
}
