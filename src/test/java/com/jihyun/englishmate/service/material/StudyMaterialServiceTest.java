package com.jihyun.englishmate.service.material;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jihyun.englishmate.dto.material.StudyMaterialUpdateRequest;
import com.jihyun.englishmate.entity.material.StudyMaterial;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.repository.material.StudyMaterialRepository;
import com.jihyun.englishmate.repository.member.MemberRepository;
import com.jihyun.englishmate.service.word.WordExtractService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyMaterialServiceTest {

    @Mock
    private StudyMaterialRepository studyMaterialRepository;

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
}
