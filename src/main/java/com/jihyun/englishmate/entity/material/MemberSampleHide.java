package com.jihyun.englishmate.entity.material;

import com.jihyun.englishmate.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원이 숨긴 SAMPLE 학습 자료를 저장합니다. SAMPLE 자체는 삭제하지 않습니다.
 */
@Entity
@Table(
        name = "member_sample_hides",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_sample_hides_member_material",
                columnNames = {"member_id", "study_material_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSampleHide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_material_id", nullable = false)
    private StudyMaterial studyMaterial;

    @Column(nullable = false, updatable = false)
    private LocalDateTime hiddenAt;

    private MemberSampleHide(Member member, StudyMaterial studyMaterial) {
        this.member = member;
        this.studyMaterial = studyMaterial;
    }

    public static MemberSampleHide hide(Member member, StudyMaterial studyMaterial) {
        return new MemberSampleHide(member, studyMaterial);
    }

    @PrePersist
    protected void onCreate() {
        this.hiddenAt = LocalDateTime.now();
    }
}
