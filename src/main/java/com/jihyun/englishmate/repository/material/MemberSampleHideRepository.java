package com.jihyun.englishmate.repository.material;

import com.jihyun.englishmate.entity.material.MemberSampleHide;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 회원별 SAMPLE 숨김 정보를 관리합니다.
 */
public interface MemberSampleHideRepository extends JpaRepository<MemberSampleHide, Long> {

    boolean existsByMemberIdAndStudyMaterialId(Long memberId, Long studyMaterialId);
}
