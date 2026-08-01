package com.jihyun.englishmate.controller.mypage;

import com.jihyun.englishmate.dto.mypage.MyPageResponse;
import com.jihyun.englishmate.dto.quiz.QuizStatisticsResponse;
import com.jihyun.englishmate.dto.review.ReviewStatisticsResponse;
import com.jihyun.englishmate.security.member.CustomUserDetails;
import com.jihyun.englishmate.service.quiz.QuizStatisticsService;
import com.jihyun.englishmate.service.review.ReviewStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인한 사용자의 학습 현황 대시보드 화면 요청을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final ReviewStatisticsService reviewStatisticsService;
    private final QuizStatisticsService quizStatisticsService;

    @GetMapping("/mypage")
    public String index(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model
    ) {
        ReviewStatisticsResponse reviewStatistics = reviewStatisticsService.getWeeklyStatistics(
                userDetails.getMemberId(),
                endDate
        );
        QuizStatisticsResponse quizStatistics = quizStatisticsService.getWeeklyStatistics(
                userDetails.getMemberId(),
                endDate
        );

        model.addAttribute("mypage", new MyPageResponse(
                userDetails.getNickname(),
                reviewStatistics.startDate(),
                reviewStatistics.endDate(),
                reviewStatistics.previousEndDate(),
                reviewStatistics.nextEndDate(),
                reviewStatistics.canMoveNext(),
                reviewStatistics,
                quizStatistics
        ));
        return "mypage/index";
    }
}
