package com.jihyun.englishmate.dto.mypage;

import com.jihyun.englishmate.dto.quiz.QuizStatisticsResponse;
import com.jihyun.englishmate.dto.review.ReviewStatisticsResponse;
import java.time.LocalDate;

/**
 * My Page 화면에 필요한 사용자 정보와 학습 통계를 묶어서 전달합니다.
 */
public record MyPageResponse(
        String nickname,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate previousEndDate,
        LocalDate nextEndDate,
        boolean canMoveNext,
        ReviewStatisticsResponse reviewStatistics,
        QuizStatisticsResponse quizStatistics
) {
}
