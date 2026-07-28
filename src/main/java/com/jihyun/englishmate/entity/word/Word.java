package com.jihyun.englishmate.entity.word;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여러 학습 자료와 단어장에서 재사용할 수 있는 단어 마스터 엔티티입니다.
 */
@Entity
@Table(name = "words")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String text;

    @Column(nullable = false, unique = true, length = 100)
    private String normalizedText;

    @Column(length = 255)
    private String meaning;

    @Column(length = 50)
    private String partOfSpeech;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Word(String text, String normalizedText) {
        this.text = text;
        this.normalizedText = normalizedText;
    }

    /**
     * 정규화된 단어를 기준으로 Word를 생성합니다.
     */
    public static Word createWord(String text, String normalizedText) {
        return new Word(text, normalizedText);
    }

    /**
     * 단어의 의미와 품사만 수정합니다.
     */
    public void updateMeaningAndPartOfSpeech(String meaning, String partOfSpeech) {
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
