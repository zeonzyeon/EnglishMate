package com.jihyun.englishmate.service.material;

import com.jihyun.englishmate.dto.material.StudyMaterialCreateRequest;
import com.jihyun.englishmate.dto.material.StudyMaterialResponse;
import com.jihyun.englishmate.dto.material.StudyMaterialUpdateRequest;
import com.jihyun.englishmate.entity.material.MemberSampleHide;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.material.StudyMaterialType;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.repository.material.MemberSampleHideRepository;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.service.word.WordExtractService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 학습 지문 CRUD와 Guest/SAMPLE 조회 정책을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final MemberSampleHideRepository memberSampleHideRepository;
    private final MemberRepository memberRepository;
    private final WordExtractService wordExtractService;

    /**
     * 로그인한 회원의 PERSONAL 학습 지문을 등록합니다.
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
     * 비회원에게는 SAMPLE 학습 자료만 반환합니다.
     */
    public List<StudyMaterialResponse> findAllForGuest() {
        return studyMaterialRepository.findAllByTypeOrderByCreatedAtDesc(StudyMaterialType.SAMPLE)
                .stream()
                .map(StudyMaterialResponse::from)
                .toList();
    }

    /**
     * 회원에게는 숨기지 않은 SAMPLE과 본인 PERSONAL 학습 자료를 함께 반환합니다.
     */
    public List<StudyMaterialResponse> findAllByMember(Long memberId) {
        return studyMaterialRepository.findVisibleMaterialsForMember(memberId)
                .stream()
                .map(StudyMaterialResponse::from)
                .toList();
    }

    /**
     * 비회원이 조회할 수 있는 SAMPLE 학습 자료 상세를 반환합니다.
     */
    public StudyMaterialResponse findSampleById(Long materialId) {
        return StudyMaterialResponse.from(findSampleMaterial(materialId));
    }

    /**
     * 회원이 볼 수 있는 SAMPLE 또는 본인 PERSONAL 학습 자료 상세를 반환합니다.
     */
    public StudyMaterialResponse findById(Long memberId, Long materialId) {
        return StudyMaterialResponse.from(findVisibleMaterial(memberId, materialId));
    }

    /**
     * 수정 가능한 본인 PERSONAL 학습 자료 상세를 반환합니다.
     */
    public StudyMaterialResponse findEditableById(Long memberId, Long materialId) {
        return StudyMaterialResponse.from(findOwnedMaterial(memberId, materialId));
    }

    /**
     * 본인 PERSONAL 학습 자료만 수정할 수 있습니다.
     */
    @Transactional
    public void update(Long memberId, Long materialId, StudyMaterialUpdateRequest request) {
        StudyMaterial material = findOwnedMaterial(memberId, materialId);
        boolean contentChanged = !Objects.equals(material.getContent(), request.content());
        material.update(request.title(), request.content());

        if (contentChanged) {
            wordExtractService.reextractMaterialWords(material);
        }
    }

    /**
     * PERSONAL은 실제 삭제하고, SAMPLE은 회원에게만 숨김 처리합니다.
     */
    @Transactional
    public void delete(Long memberId, Long materialId) {
        StudyMaterial material = findVisibleMaterial(memberId, materialId);
        if (material.isSample()) {
            hideSample(memberId, material);
            return;
        }

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

    private StudyMaterial findVisibleMaterial(Long memberId, Long materialId) {
        return studyMaterialRepository.findVisibleMaterialForMember(memberId, materialId)
                .orElseThrow(() -> new EntityNotFoundException("학습 자료를 찾을 수 없습니다."));
    }

    private StudyMaterial findSampleMaterial(Long materialId) {
        return studyMaterialRepository.findByIdAndType(materialId, StudyMaterialType.SAMPLE)
                .orElseThrow(() -> new EntityNotFoundException("학습 자료를 찾을 수 없습니다."));
    }

    private void hideSample(Long memberId, StudyMaterial material) {
        if (memberSampleHideRepository.existsByMemberIdAndStudyMaterialId(memberId, material.getId())) {
            return;
        }

        Member member = findMember(memberId);
        memberSampleHideRepository.save(MemberSampleHide.hide(member, material));
    }
}
