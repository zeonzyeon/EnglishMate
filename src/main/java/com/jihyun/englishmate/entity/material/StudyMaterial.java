package com.jihyun.englishmate.entity.material;

import com.jihyun.englishmate.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 입력한 영어 학습 지문을 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "study_materials")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StudyMaterialType type;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private StudyMaterial(Member member, StudyMaterialType type, String title, String content) {
        this.member = member;
        this.type = type;
        this.title = title;
        this.content = content;
    }

    /**
     * 학습 지문 등록 시 사용할 생성 메서드입니다.
     */
    public static StudyMaterial createStudyMaterial(Member member, String title, String content) {
        return new StudyMaterial(member, StudyMaterialType.PERSONAL, title, content);
    }

    /**
     * 비회원과 회원 모두에게 제공할 공용 예시 지문을 생성합니다.
     */
    public static StudyMaterial createSample(Member sampleOwner, String title, String content) {
        return new StudyMaterial(sampleOwner, StudyMaterialType.SAMPLE, title, content);
    }

    /**
     * 학습 지문의 제목과 본문을 수정합니다.
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public boolean isSample() {
        return type == StudyMaterialType.SAMPLE;
    }

    public boolean isPersonal() {
        return type == StudyMaterialType.PERSONAL || type == null;
    }

    @PrePersist
    protected void onCreate() {
        if (this.type == null) {
            this.type = StudyMaterialType.PERSONAL;
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
