package com.jihyun.englishmate.entity.word;

import com.jihyun.englishmate.entity.material.StudyMaterial;
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
 * 특정 학습 지문에서 추출된 단어 정보를 저장하는 연결 엔티티입니다.
 */
@Entity
@Table(
        name = "material_words",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_material_words_material_word",
                        columnNames = {"study_material_id", "word_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_material_id", nullable = false)
    private StudyMaterial studyMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(nullable = false)
    private int frequency;

    @Column(length = 500)
    private String exampleSentence;

    @Column(length = 255)
    private String meaning;

    @Column(length = 50)
    private String partOfSpeech;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private MaterialWord(StudyMaterial studyMaterial, Word word, int frequency) {
        this.studyMaterial = studyMaterial;
        this.word = word;
        this.frequency = frequency;
    }

    /**
     * 학습 지문과 단어를 연결합니다.
     */
    public static MaterialWord createMaterialWord(StudyMaterial studyMaterial, Word word, int frequency) {
        return new MaterialWord(studyMaterial, word, frequency);
    }

    /**
     * 재추출 시 현재 본문 기준의 단어 빈도로 갱신합니다.
     */
    public void updateFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void updateMeaningAndPartOfSpeech(String meaning, String partOfSpeech) {
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
