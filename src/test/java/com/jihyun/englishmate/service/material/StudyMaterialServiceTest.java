package com.jihyun.englishmate.service.material;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StudyMaterialServiceTest {

    @Mock
    private StudyMaterialRepository studyMaterialRepository;

    @Mock
    private MemberSampleHideRepository memberSampleHideRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WordExtractService wordExtractService;

    @InjectMocks
    private StudyMaterialService studyMaterialService;

    @Test
    @DisplayName("본문이 변경되지 않으면 기존 MaterialWord를 유지한다")
    void updateTitleOnlyKeepsMaterialWords() {
        Member member = Member.createMember("member@test.com", "password", "tester");
        StudyMaterial material = StudyMaterial.createStudyMaterial(member, "old title", "same content");
        when(studyMaterialRepository.findByIdAndMemberId(1L, 1L)).thenReturn(Optional.of(material));

        studyMaterialService.update(1L, 1L, new StudyMaterialUpdateRequest("new title", "same content"));

        verify(wordExtractService, never()).reextractMaterialWords(material);
    }

    @Test
    @DisplayName("본문이 변경되면 수정된 본문 기준으로 MaterialWord를 재추출한다")
    void updateContentReextractsMaterialWords() {
        Member member = Member.createMember("member@test.com", "password", "tester");
        StudyMaterial material = StudyMaterial.createStudyMaterial(member, "title", "old content");
        when(studyMaterialRepository.findByIdAndMemberId(1L, 1L)).thenReturn(Optional.of(material));

        studyMaterialService.update(1L, 1L, new StudyMaterialUpdateRequest("title", "new content"));

        verify(wordExtractService).reextractMaterialWords(material);
    }

    @Test
    @DisplayName("비회원은 SAMPLE 학습 자료만 조회한다")
    void guestFindsSampleMaterialsOnly() {
        Member sampleOwner = Member.createMember("sample@englishmate.local", "password", "sample");
        StudyMaterial sample = StudyMaterial.createSample(sampleOwner, "sample", "sample content");
        when(studyMaterialRepository.findAllByTypeOrderByCreatedAtDesc(StudyMaterialType.SAMPLE))
                .thenReturn(List.of(sample));

        List<StudyMaterialResponse> responses = studyMaterialService.findAllForGuest();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).sample()).isTrue();
    }

    @Test
    @DisplayName("회원이 SAMPLE을 삭제하면 실제 삭제하지 않고 숨김 정보만 저장한다")
    void deleteSampleHidesForMemberOnly() {
        Member member = Member.createMember("member@test.com", "password", "tester");
        Member sampleOwner = Member.createMember("sample@englishmate.local", "password", "sample");
        StudyMaterial sample = StudyMaterial.createSample(sampleOwner, "sample", "sample content");
        ReflectionTestUtils.setField(sample, "id", 10L);
        when(studyMaterialRepository.findVisibleMaterialForMember(1L, 10L)).thenReturn(Optional.of(sample));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberSampleHideRepository.existsByMemberIdAndStudyMaterialId(1L, 10L)).thenReturn(false);

        studyMaterialService.delete(1L, 10L);

        verify(studyMaterialRepository, never()).delete(sample);
        verify(memberSampleHideRepository).save(any(MemberSampleHide.class));
    }
}
