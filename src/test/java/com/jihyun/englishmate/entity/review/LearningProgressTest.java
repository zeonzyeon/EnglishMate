package com.jihyun.englishmate.entity.review;

import static org.assertj.core.api.Assertions.assertThat;

import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.entity.word.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LearningProgressTest {

    @Test
    @DisplayName("REMEMBERED 응답은 복습 수와 연속 암기 수를 증가시킨다")
    void remember() {
        LearningProgress progress = LearningProgress.create(createVocabulary());

        progress.remember();

        assertThat(progress.getReviewCount()).isEqualTo(1);
        assertThat(progress.getRememberedCount()).isEqualTo(1);
        assertThat(progress.getDifficultCount()).isZero();
        assertThat(progress.getConsecutiveRememberedCount()).isEqualTo(1);
        assertThat(progress.getLastReviewedAt()).isNotNull();
        assertThat(progress.isMastered()).isFalse();
    }

    @Test
    @DisplayName("DIFFICULT 응답은 어려움 수를 증가시키고 연속 암기 수를 초기화한다")
    void markDifficult() {
        LearningProgress progress = LearningProgress.create(createVocabulary());
        progress.remember();

        progress.markDifficult();

        assertThat(progress.getReviewCount()).isEqualTo(2);
        assertThat(progress.getRememberedCount()).isEqualTo(1);
        assertThat(progress.getDifficultCount()).isEqualTo(1);
        assertThat(progress.getConsecutiveRememberedCount()).isZero();
        assertThat(progress.getLastReviewedAt()).isNotNull();
        assertThat(progress.isMastered()).isFalse();
    }

    private Vocabulary createVocabulary() {
        Member member = Member.createMember("member@test.com", "password", "tester");
        Word word = Word.createWord("apple", "apple");
        return Vocabulary.createVocabulary(member, word);
    }
}
