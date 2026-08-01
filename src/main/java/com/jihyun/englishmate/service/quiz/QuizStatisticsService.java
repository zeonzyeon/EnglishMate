package com.jihyun.englishmate.service.quiz;

import com.jihyun.englishmate.dto.quiz.DailyQuizStatisticsResponse;
import com.jihyun.englishmate.dto.quiz.QuizPeriodSummaryResponse;
import com.jihyun.englishmate.dto.quiz.QuizStatisticsResponse;
import com.jihyun.englishmate.entity.quiz.QuizAttempt;
import com.jihyun.englishmate.repository.quiz.QuizAttemptRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * My Page에서 보여줄 주간 퀴즈 통계를 계산합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizStatisticsService {

    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Seoul");
    private static final int PERIOD_DAYS = 7;

    private final QuizAttemptRepository quizAttemptRepository;

    public QuizStatisticsResponse getWeeklyStatistics(Long memberId, String endDateText) {
        LocalDate today = LocalDate.now(USER_ZONE);
        LocalDate endDate = parseEndDate(endDateText, today);
        LocalDate startDate = endDate.minusDays(PERIOD_DAYS - 1L);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime exclusiveEndDateTime = endDate.plusDays(1).atStartOfDay();

        List<QuizAttempt> attempts = quizAttemptRepository.findCompletedAttemptsForStatistics(
                memberId,
                startDateTime,
                exclusiveEndDateTime
        );

        List<DailyQuizStatisticsResponse> dailyStatistics = buildDailyStatistics(startDate, today, attempts);
        QuizPeriodSummaryResponse summary = buildSummary(startDate, endDate, dailyStatistics);
        boolean canMoveNext = endDate.isBefore(today);
        LocalDate nextEndDate = endDate.plusDays(PERIOD_DAYS).isAfter(today)
                ? today
                : endDate.plusDays(PERIOD_DAYS);

        return new QuizStatisticsResponse(
                startDate,
                endDate,
                startDate.minusDays(1),
                nextEndDate,
                canMoveNext,
                dailyStatistics,
                summary
        );
    }

    private LocalDate parseEndDate(String endDateText, LocalDate today) {
        if (endDateText == null || endDateText.isBlank()) {
            return today;
        }

        try {
            LocalDate parsedDate = LocalDate.parse(endDateText);
            return parsedDate.isAfter(today) ? today : parsedDate;
        } catch (DateTimeParseException e) {
            return today;
        }
    }

    private List<DailyQuizStatisticsResponse> buildDailyStatistics(
            LocalDate startDate,
            LocalDate today,
            List<QuizAttempt> attempts
    ) {
        Map<LocalDate, DayAccumulator> accumulatorByDate = new HashMap<>();
        for (QuizAttempt attempt : attempts) {
            LocalDate completedDate = attempt.getCompletedAt().toLocalDate();
            DayAccumulator accumulator = accumulatorByDate.computeIfAbsent(completedDate, key -> new DayAccumulator());
            accumulator.add(attempt);
        }

        int maxSolvedQuestionCount = accumulatorByDate.values()
                .stream()
                .mapToInt(DayAccumulator::solvedQuestionCount)
                .max()
                .orElse(0);

        List<DailyQuizStatisticsResponse> dailyStatistics = new ArrayList<>();
        for (int i = 0; i < PERIOD_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            DayAccumulator accumulator = accumulatorByDate.getOrDefault(date, new DayAccumulator());
            int solvedQuestionCount = accumulator.solvedQuestionCount();
            int barHeightPercent = maxSolvedQuestionCount == 0
                    ? 0
                    : (int) Math.round((solvedQuestionCount * 100.0) / maxSolvedQuestionCount);

            dailyStatistics.add(new DailyQuizStatisticsResponse(
                    date,
                    koreanDayOfWeek(date.getDayOfWeek()),
                    accumulator.quizAttemptCount(),
                    solvedQuestionCount,
                    accumulator.correctCount(),
                    accumulator.wrongCount(),
                    calculateRate(accumulator.correctCount(), solvedQuestionCount),
                    barHeightPercent,
                    date.equals(today)
            ));
        }
        return dailyStatistics;
    }

    private QuizPeriodSummaryResponse buildSummary(
            LocalDate startDate,
            LocalDate endDate,
            List<DailyQuizStatisticsResponse> dailyStatistics
    ) {
        int quizAttemptCount = dailyStatistics.stream()
                .mapToInt(DailyQuizStatisticsResponse::quizAttemptCount)
                .sum();
        int solvedQuestionCount = dailyStatistics.stream()
                .mapToInt(DailyQuizStatisticsResponse::solvedQuestionCount)
                .sum();
        int correctCount = dailyStatistics.stream()
                .mapToInt(DailyQuizStatisticsResponse::correctCount)
                .sum();
        int wrongCount = dailyStatistics.stream()
                .mapToInt(DailyQuizStatisticsResponse::wrongCount)
                .sum();

        return new QuizPeriodSummaryResponse(
                startDate,
                endDate,
                quizAttemptCount,
                solvedQuestionCount,
                correctCount,
                wrongCount,
                calculateRate(correctCount, solvedQuestionCount)
        );
    }

    private int calculateRate(int correctCount, int solvedQuestionCount) {
        if (solvedQuestionCount == 0) {
            return 0;
        }
        return (int) Math.round((correctCount * 100.0) / solvedQuestionCount);
    }

    private String koreanDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    private static class DayAccumulator {

        private int quizAttemptCount;
        private int solvedQuestionCount;
        private int correctCount;
        private int wrongCount;

        private void add(QuizAttempt attempt) {
            quizAttemptCount++;
            solvedQuestionCount += attempt.getTotalQuestions();
            correctCount += attempt.getCorrectCount();
            wrongCount += attempt.getWrongCount();
        }

        private int quizAttemptCount() {
            return quizAttemptCount;
        }

        private int solvedQuestionCount() {
            return solvedQuestionCount;
        }

        private int correctCount() {
            return correctCount;
        }

        private int wrongCount() {
            return wrongCount;
        }
    }
}
