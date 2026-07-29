package com.jihyun.englishmate.service.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.jihyun.englishmate.dto.review.ReviewStatisticsResponse;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.review.ReviewCardResult;
import com.jihyun.englishmate.entity.review.ReviewResponseType;
import com.jihyun.englishmate.entity.review.ReviewSession;
import com.jihyun.englishmate.entity.vocabulary.Vocabulary;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.repository.review.ReviewCardResultRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewStatisticsServiceTest {

    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Seoul");

    @Mock
    private ReviewCardResultRepository reviewCardResultRepository;

    @InjectMocks
    private ReviewStatisticsService reviewStatisticsService;

    @Test
    @DisplayName("기록이 없어도 최근 7일을 0 통계로 반환한다")
    void emptyStatisticsContainsSevenDays() {
        LocalDate today = LocalDate.now(USER_ZONE);
        when(reviewCardResultRepository.findAnsweredCardsForStatistics(eq(1L), any(), any()))
                .thenReturn(List.of());

        ReviewStatisticsResponse response = reviewStatisticsService.getWeeklyStatistics(1L, null);

        assertThat(response.startDate()).isEqualTo(today.minusDays(6));
        assertThat(response.endDate()).isEqualTo(today);
        assertThat(response.canMoveNext()).isFalse();
        assertThat(response.dailyStatistics()).hasSize(7);
        assertThat(response.summary().totalReviewCount()).isZero();
        assertThat(response.summary().rememberedRate()).isZero();
        assertThat(response.summary().mostReviewedDate()).isNull();
    }

    @Test
    @DisplayName("기간 전체 고유 단어 수는 날짜별 합이 아니라 distinct Vocabulary 기준으로 계산한다")
    void aggregateWeeklyStatistics() {
        LocalDate today = LocalDate.now(USER_ZONE);
        Vocabulary vocabulary1 = createVocabulary(1L, "apple");
        Vocabulary vocabulary2 = createVocabulary(2L, "book");
        List<ReviewCardResult> cards = List.of(
                createCard(vocabulary1, ReviewResponseType.REMEMBERED, today.atTime(9, 0)),
                createCard(vocabulary1, ReviewResponseType.DIFFICULT, today.atTime(15, 0)),
                createCard(vocabulary2, ReviewResponseType.REMEMBERED, today.minusDays(1).atTime(10, 0))
        );
        when(reviewCardResultRepository.findAnsweredCardsForStatistics(eq(1L), any(), any()))
                .thenReturn(cards);

        ReviewStatisticsResponse response = reviewStatisticsService.getWeeklyStatistics(1L, today.toString());

        assertThat(response.summary().totalReviewCount()).isEqualTo(3);
        assertThat(response.summary().rememberedCount()).isEqualTo(2);
        assertThat(response.summary().difficultCount()).isEqualTo(1);
        assertThat(response.summary().uniqueVocabularyCount()).isEqualTo(2);
        assertThat(response.summary().rememberedRate()).isEqualTo(67);
        assertThat(response.summary().mostReviewedDate()).isEqualTo(today);
        assertThat(response.dailyStatistics().get(5).totalReviewCount()).isEqualTo(1);
        assertThat(response.dailyStatistics().get(6).totalReviewCount()).isEqualTo(2);
        assertThat(response.dailyStatistics().get(6).uniqueVocabularyCount()).isEqualTo(1);
    }

    private ReviewCardResult createCard(
            Vocabulary vocabulary,
            ReviewResponseType responseType,
            LocalDateTime answeredAt
    ) {
        Member member = Member.createMember("member@test.com", "password", "tester");
        ReviewSession session = ReviewSession.start(member, 1);
        ReviewCardResult card = ReviewCardResult.create(session, vocabulary, 1);
        card.answer(responseType);
        setField(card, "answeredAt", answeredAt);
        return card;
    }

    private Vocabulary createVocabulary(Long id, String text) {
        Member member = Member.createMember("member@test.com", "password", "tester");
        Word word = Word.createWord(text, text);
        Vocabulary vocabulary = Vocabulary.createVocabulary(member, word);
        setField(vocabulary, "id", id);
        return vocabulary;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
