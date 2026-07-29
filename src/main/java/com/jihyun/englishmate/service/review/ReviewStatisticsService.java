package com.jihyun.englishmate.service.review;

import com.jihyun.englishmate.dto.review.DailyReviewStatisticsResponse;
import com.jihyun.englishmate.dto.review.ReviewPeriodSummaryResponse;
import com.jihyun.englishmate.dto.review.ReviewStatisticsResponse;
import com.jihyun.englishmate.entity.review.ReviewCardResult;
import com.jihyun.englishmate.entity.review.ReviewResponseType;
import com.jihyun.englishmate.repository.review.ReviewCardResultRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Review 페이지의 주간 복습 통계 계산을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewStatisticsService {

    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Seoul");
    private static final int PERIOD_DAYS = 7;

    private final ReviewCardResultRepository reviewCardResultRepository;

    public ReviewStatisticsResponse getWeeklyStatistics(Long memberId, String endDateText) {
        LocalDate today = LocalDate.now(USER_ZONE);
        LocalDate endDate = parseEndDate(endDateText, today);
        LocalDate startDate = endDate.minusDays(PERIOD_DAYS - 1L);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime exclusiveEndDateTime = endDate.plusDays(1).atStartOfDay();

        List<ReviewCardResult> cards = reviewCardResultRepository.findAnsweredCardsForStatistics(
                memberId,
                startDateTime,
                exclusiveEndDateTime
        );

        List<DailyReviewStatisticsResponse> dailyStatistics = buildDailyStatistics(startDate, today, cards);
        ReviewPeriodSummaryResponse summary = buildSummary(startDate, endDate, dailyStatistics, cards);
        boolean canMoveNext = endDate.isBefore(today);
        LocalDate nextEndDate = endDate.plusDays(PERIOD_DAYS).isAfter(today)
                ? today
                : endDate.plusDays(PERIOD_DAYS);

        return new ReviewStatisticsResponse(
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

    private List<DailyReviewStatisticsResponse> buildDailyStatistics(
            LocalDate startDate,
            LocalDate today,
            List<ReviewCardResult> cards
    ) {
        Map<LocalDate, DayAccumulator> accumulatorByDate = new HashMap<>();
        for (ReviewCardResult card : cards) {
            LocalDate answeredDate = card.getAnsweredAt().toLocalDate();
            DayAccumulator accumulator = accumulatorByDate.computeIfAbsent(answeredDate, key -> new DayAccumulator());
            accumulator.add(card);
        }

        int maxTotalReviewCount = accumulatorByDate.values()
                .stream()
                .mapToInt(DayAccumulator::totalReviewCount)
                .max()
                .orElse(0);

        List<DailyReviewStatisticsResponse> dailyStatistics = new ArrayList<>();
        for (int i = 0; i < PERIOD_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            DayAccumulator accumulator = accumulatorByDate.getOrDefault(date, new DayAccumulator());
            int totalReviewCount = accumulator.totalReviewCount();
            int rememberedRate = calculateRate(accumulator.rememberedCount(), totalReviewCount);
            int barHeightPercent = maxTotalReviewCount == 0
                    ? 0
                    : (int) Math.round((totalReviewCount * 100.0) / maxTotalReviewCount);

            dailyStatistics.add(new DailyReviewStatisticsResponse(
                    date,
                    koreanDayOfWeek(date.getDayOfWeek()),
                    totalReviewCount,
                    accumulator.rememberedCount(),
                    accumulator.difficultCount(),
                    accumulator.uniqueVocabularyCount(),
                    rememberedRate,
                    barHeightPercent,
                    date.equals(today)
            ));
        }
        return dailyStatistics;
    }

    private ReviewPeriodSummaryResponse buildSummary(
            LocalDate startDate,
            LocalDate endDate,
            List<DailyReviewStatisticsResponse> dailyStatistics,
            List<ReviewCardResult> cards
    ) {
        int totalReviewCount = dailyStatistics.stream()
                .mapToInt(DailyReviewStatisticsResponse::totalReviewCount)
                .sum();
        int rememberedCount = dailyStatistics.stream()
                .mapToInt(DailyReviewStatisticsResponse::rememberedCount)
                .sum();
        int difficultCount = dailyStatistics.stream()
                .mapToInt(DailyReviewStatisticsResponse::difficultCount)
                .sum();
        int activeReviewDays = (int) dailyStatistics.stream()
                .filter(day -> day.totalReviewCount() > 0)
                .count();
        int uniqueVocabularyCount = (int) cards.stream()
                .map(card -> card.getVocabulary().getId())
                .distinct()
                .count();
        int rememberedRate = calculateRate(rememberedCount, totalReviewCount);

        DailyReviewStatisticsResponse mostReviewedDay = dailyStatistics.stream()
                .filter(day -> day.totalReviewCount() > 0)
                .max(Comparator
                        .comparingInt(DailyReviewStatisticsResponse::totalReviewCount)
                        .thenComparing(DailyReviewStatisticsResponse::date))
                .orElse(null);

        return new ReviewPeriodSummaryResponse(
                startDate,
                endDate,
                totalReviewCount,
                rememberedCount,
                difficultCount,
                uniqueVocabularyCount,
                activeReviewDays,
                rememberedRate,
                mostReviewedDay == null ? null : mostReviewedDay.date(),
                mostReviewedDay == null ? 0 : mostReviewedDay.totalReviewCount()
        );
    }

    private int calculateRate(int rememberedCount, int totalReviewCount) {
        if (totalReviewCount == 0) {
            return 0;
        }
        return (int) Math.round((rememberedCount * 100.0) / totalReviewCount);
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

        private int totalReviewCount;
        private int rememberedCount;
        private int difficultCount;
        private final Set<Long> vocabularyIds = new HashSet<>();

        private void add(ReviewCardResult card) {
            totalReviewCount++;
            vocabularyIds.add(card.getVocabulary().getId());
            if (card.getResponseType() == ReviewResponseType.REMEMBERED) {
                rememberedCount++;
            } else if (card.getResponseType() == ReviewResponseType.DIFFICULT) {
                difficultCount++;
            }
        }

        private int totalReviewCount() {
            return totalReviewCount;
        }

        private int rememberedCount() {
            return rememberedCount;
        }

        private int difficultCount() {
            return difficultCount;
        }

        private int uniqueVocabularyCount() {
            return vocabularyIds.size();
        }
    }
}
