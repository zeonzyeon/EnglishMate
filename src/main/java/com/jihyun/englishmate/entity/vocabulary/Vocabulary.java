package com.jihyun.englishmate.entity.vocabulary;

import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.word.Word;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원이 개인 단어장에 저장한 단어를 관리하는 엔티티입니다.
 */
@Entity
@Table(
        name = "vocabularies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vocabularies_member_word",
                        columnNames = {"member_id", "word_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(nullable = false)
    private int masteryLevel;

    @Column(nullable = false)
    private int wrongCount;

    @Column(nullable = false)
    private int correctCount;

    private LocalDateTime lastStudiedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Vocabulary(Member member, Word word) {
        this.member = member;
        this.word = word;
        this.masteryLevel = 0;
        this.wrongCount = 0;
        this.correctCount = 0;
    }

    /**
     * 회원과 단어를 연결해 개인 단어장 항목을 생성합니다.
     */
    public static Vocabulary createVocabulary(Member member, Word word) {
        return new Vocabulary(member, word);
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
