package com.jihyun.englishmate.controller.member;

import com.jihyun.englishmate.dto.member.MemberRequest;
import com.jihyun.englishmate.exception.member.DuplicateEmailException;
import com.jihyun.englishmate.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 회원가입 화면과 요청을 처리하는 컨트롤러입니다.
 */
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 화면을 반환합니다.
     */
    @GetMapping({"/signup", "/members/signup"})
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new MemberRequest.Signup("", "", ""));
        return "signup";
    }

    /**
     * 회원가입 요청을 검증하고 서비스 계층에 처리를 위임합니다.
     */
    @PostMapping({"/signup", "/members/signup"})
    public String signup(
            @Valid @ModelAttribute("signupForm") MemberRequest.Signup request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        try {
            memberService.signup(request);
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "signup";
        }

        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/login";
    }
}
