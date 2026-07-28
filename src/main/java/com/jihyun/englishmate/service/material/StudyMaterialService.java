package com.jihyun.englishmate.service.material;

import com.jihyun.englishmate.dto.material.StudyMaterialCreateRequest;
import com.jihyun.englishmate.dto.material.StudyMaterialResponse;
import com.jihyun.englishmate.dto.material.StudyMaterialUpdateRequest;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.service.word.WordExtractService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 학습 지문 CRUD와 소유자 검증을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final MemberRepository memberRepository;
    private final WordExtractService wordExtractService;

    /**
     * 로그인한 회원의 학습 지문을 등록합니다.
     */
    @Transactional
    public Long create(Long memberId, StudyMaterialCreateRequest request) {
        Member member = findMember(memberId);
        StudyMaterial material = StudyMaterial.createStudyMaterial(member, request.title(), request.content());
        Long materialId = studyMaterialRepository.save(material).getId();
        wordExtractService.extractAndSave(memberId, materialId);
        return materialId;
    }

    /**
     * 로그인한 회원이 등록한 학습 지문 목록만 반환합니다.
     */
    public List<StudyMaterialResponse> findAllByMember(Long memberId) {
        return studyMaterialRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(StudyMaterialResponse::from)
                .toList();
    }

    /**
     * 본인 소유의 학습 지문 상세 정보만 반환합니다.
     */
    public StudyMaterialResponse findById(Long memberId, Long materialId) {
        return StudyMaterialResponse.from(findOwnedMaterial(memberId, materialId));
    }

    /**
     * 본인 소유의 학습 지문만 수정합니다.
     */
    @Transactional
    public void update(Long memberId, Long materialId, StudyMaterialUpdateRequest request) {
        StudyMaterial material = findOwnedMaterial(memberId, materialId);
        material.update(request.title(), request.content());
    }

    /**
     * 본인 소유의 학습 지문만 삭제합니다.
     */
    @Transactional
    public void delete(Long memberId, Long materialId) {
        StudyMaterial material = findOwnedMaterial(memberId, materialId);
        studyMaterialRepository.delete(material);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
    }

    private StudyMaterial findOwnedMaterial(Long memberId, Long materialId) {
        return studyMaterialRepository.findByIdAndMemberId(materialId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("학습 자료를 찾을 수 없습니다."));
    }
}
