package com.jihyun.englishmate.service.quiz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.jihyun.englishmate.dto.quiz.QuizStatisticsResponse;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.entity.quiz.QuizAttempt;
import com.jihyun.englishmate.entity.quiz.QuizType;
import com.jihyun.englishmate.repository.quiz.QuizAttemptRepository;
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
class QuizStatisticsServiceTest {

    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Seoul");

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @InjectMocks
    private QuizStatisticsService quizStatisticsService;

    @Test
    @DisplayName("퀴즈 기록이 없으면 최근 7일을 0 통계로 반환한다")
    void emptyStatisticsContainsSevenDays() {
        LocalDate today = LocalDate.now(USER_ZONE);
        when(quizAttemptRepository.findCompletedAttemptsForStatistics(eq(1L), any(), any()))
                .thenReturn(List.of());

        QuizStatisticsResponse response = quizStatisticsService.getWeeklyStatistics(1L, null);

        assertThat(response.startDate()).isEqualTo(today.minusDays(6));
        assertThat(response.endDate()).isEqualTo(today);
        assertThat(response.canMoveNext()).isFalse();
        assertThat(response.dailyStatistics()).hasSize(7);
        assertThat(response.summary().quizAttemptCount()).isZero();
        assertThat(response.summary().averageCorrectRate()).isZero();
    }

    @Test
    @DisplayName("완료된 퀴즈 응시 기록을 일별/기간 요약으로 집계한다")
    void aggregateWeeklyStatistics() {
        LocalDate today = LocalDate.now(USER_ZONE);
        List<QuizAttempt> attempts = List.of(
                createCompletedAttempt(5, 4, 1, today.atTime(9, 0)),
                createCompletedAttempt(5, 3, 2, today.atTime(15, 0)),
                createCompletedAttempt(4, 2, 2, today.minusDays(1).atTime(10, 0))
        );
        when(quizAttemptRepository.findCompletedAttemptsForStatistics(eq(1L), any(), any()))
                .thenReturn(attempts);

        QuizStatisticsResponse response = quizStatisticsService.getWeeklyStatistics(1L, today.toString());

        assertThat(response.summary().quizAttemptCount()).isEqualTo(3);
        assertThat(response.summary().solvedQuestionCount()).isEqualTo(14);
        assertThat(response.summary().correctCount()).isEqualTo(9);
        assertThat(response.summary().wrongCount()).isEqualTo(5);
        assertThat(response.summary().averageCorrectRate()).isEqualTo(64);
        assertThat(response.dailyStatistics().get(5).quizAttemptCount()).isEqualTo(1);
        assertThat(response.dailyStatistics().get(5).solvedQuestionCount()).isEqualTo(4);
        assertThat(response.dailyStatistics().get(6).quizAttemptCount()).isEqualTo(2);
        assertThat(response.dailyStatistics().get(6).solvedQuestionCount()).isEqualTo(10);
    }

    private QuizAttempt createCompletedAttempt(
            int totalQuestions,
            int correctCount,
            int wrongCount,
            LocalDateTime completedAt
    ) {
        Member member = Member.createMember("member@test.com", "password", "tester");
        QuizAttempt attempt = QuizAttempt.start(member, QuizType.WORD_TO_MEANING_MULTIPLE_CHOICE, totalQuestions);
        for (int i = 0; i < correctCount; i++) {
            attempt.recordAnswer(true);
        }
        for (int i = 0; i < wrongCount; i++) {
            attempt.recordAnswer(false);
        }
        attempt.complete();
        setField(attempt, "completedAt", completedAt);
        return attempt;
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
