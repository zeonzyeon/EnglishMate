package com.jihyun.englishmate.controller.review;

import com.jihyun.englishmate.dto.review.ReviewAnswerRequest;
import com.jihyun.englishmate.dto.review.ReviewCardResponse;
import com.jihyun.englishmate.dto.review.ReviewStartRequest;
import com.jihyun.englishmate.security.member.CustomUserDetails;
import com.jihyun.englishmate.service.review.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
 * 플래시카드 복습 화면 요청을 처리합니다.
 */
@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        addIndexAttributes(userDetails.getMemberId(), model);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ReviewStartRequest(false, List.of()));
        }
        return "review/index";
    }

    @PostMapping("/start")
    public String start(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("form") ReviewStartRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addIndexAttributes(userDetails.getMemberId(), model);
            return "review/index";
        }

        try {
            Long sessionId = reviewService.start(userDetails.getMemberId(), request);
            return "redirect:/review/" + sessionId + "/card";
        } catch (IllegalArgumentException e) {
            addIndexAttributes(userDetails.getMemberId(), model);
            model.addAttribute("warningMessage", e.getMessage());
            return "review/index";
        }
    }

    @GetMapping("/{sessionId}/card")
    public String card(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sessionId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ReviewCardResponse card = reviewService.findCurrentCard(userDetails.getMemberId(), sessionId);
            model.addAttribute("card", card);
            return "review/card";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/review";
        }
    }

    @PostMapping("/{sessionId}/answer")
    public String answer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sessionId,
            @ModelAttribute ReviewAnswerRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean completed = reviewService.answer(userDetails.getMemberId(), sessionId, request);
            return completed ? "redirect:/review/" + sessionId + "/complete" : "redirect:/review/" + sessionId + "/card";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/review/" + sessionId + "/card";
        }
    }

    @GetMapping("/{sessionId}/complete")
    public String complete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sessionId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            model.addAttribute("result", reviewService.findCompleteResult(userDetails.getMemberId(), sessionId));
            return "review/complete";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/review";
        }
    }

    private void addIndexAttributes(Long memberId, Model model) {
        model.addAttribute("scopeItems", reviewService.findScopeItems(memberId));
    }
}
