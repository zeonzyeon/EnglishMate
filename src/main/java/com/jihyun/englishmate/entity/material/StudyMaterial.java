package com.jihyun.englishmate.entity.material;

import com.jihyun.englishmate.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private StudyMaterial(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
    }

    /**
     * 학습 지문 등록 시 사용할 생성 메서드입니다.
     */
    public static StudyMaterial createStudyMaterial(Member member, String title, String content) {
        return new StudyMaterial(member, title, content);
    }

    /**
     * 학습 지문의 제목과 본문을 수정합니다.
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
