package com.jihyun.englishmate.controller.quiz;

import com.jihyun.englishmate.dto.quiz.QuizAnswerRequest;
import com.jihyun.englishmate.dto.quiz.QuizAnswerResponse;
import com.jihyun.englishmate.dto.quiz.QuizQuestionResponse;
import com.jihyun.englishmate.dto.quiz.QuizScopeItemResponse;
import com.jihyun.englishmate.dto.quiz.QuizStartRequest;
import com.jihyun.englishmate.dto.quiz.QuizTypeResponse;
import com.jihyun.englishmate.entity.quiz.QuizType;
import com.jihyun.englishmate.security.member.CustomUserDetails;
import com.jihyun.englishmate.service.quiz.QuizService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 퀴즈 시작, 문제 풀이, 결과 화면 요청을 처리합니다.
 */
@Controller
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * 퀴즈 유형과 출제 범위 선택 화면을 표시합니다.
     */
    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        addIndexAttributes(userDetails.getMemberId(), model);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new QuizStartRequest(null, false, List.of()));
        }
        return "quiz/index";
    }

    /**
     * 퀴즈를 시작하고 첫 번째 문제로 이동합니다.
     */
    @PostMapping("/start")
    public String start(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("form") QuizStartRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addIndexAttributes(userDetails.getMemberId(), model);
            return "quiz/index";
        }

        try {
            Long attemptId = quizService.start(userDetails.getMemberId(), request);
            return "redirect:/quiz/attempts/" + attemptId + "/questions/1";
        } catch (IllegalArgumentException e) {
            addIndexAttributes(userDetails.getMemberId(), model);
            model.addAttribute("warningMessage", e.getMessage());
            return "quiz/index";
        } catch (EntityNotFoundException e) {
            addIndexAttributes(userDetails.getMemberId(), model);
            model.addAttribute("warningMessage", e.getMessage());
            return "quiz/index";
        }
    }

    /**
     * 현재 순서의 문제를 표시합니다.
     */
    @GetMapping("/attempts/{attemptId}/questions/{questionOrder}")
    public String question(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long attemptId,
            @PathVariable int questionOrder,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            QuizQuestionResponse question = quizService.findQuestion(userDetails.getMemberId(), attemptId, questionOrder);
            model.addAttribute("question", question);
            model.addAttribute("form", new QuizAnswerRequest(""));
            return "quiz/question";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/quiz";
        }
    }

    /**
     * 답안을 제출하고 같은 문제 화면에서 채점 결과를 보여줍니다.
     */
    @PostMapping("/attempts/{attemptId}/questions/{questionOrder}/answer")
    public String answer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long attemptId,
            @PathVariable int questionOrder,
            @ModelAttribute("form") QuizAnswerRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            QuizAnswerResponse answer = quizService.submitAnswer(
                    userDetails.getMemberId(),
                    attemptId,
                    questionOrder,
                    request
            );
            if (answer.lastQuestion()) {
                return "redirect:/quiz/attempts/" + attemptId + "/result";
            }
            return "redirect:/quiz/attempts/" + attemptId + "/questions/" + (questionOrder + 1);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/quiz/attempts/" + attemptId + "/questions/" + questionOrder;
        }
    }

    /**
     * 퀴즈 최종 결과 화면을 표시합니다.
     */
    @GetMapping("/attempts/{attemptId}/result")
    public String result(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long attemptId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            model.addAttribute("result", quizService.findResult(userDetails.getMemberId(), attemptId));
            return "quiz/result";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/quiz";
        }
    }

    private void addIndexAttributes(Long memberId, Model model) {
        List<QuizTypeResponse> quizTypes = Arrays.stream(QuizType.values())
                .map(QuizTypeResponse::from)
                .toList();
        List<QuizScopeItemResponse> scopeItems = quizService.findScopeItems(memberId);
        model.addAttribute("quizTypes", quizTypes);
        model.addAttribute("scopeItems", scopeItems);
    }
}
