package com.jihyun.englishmate.controller.home;

import com.jihyun.englishmate.security.member.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로그인 후 접근하는 메인 화면을 처리합니다.
 */
@Controller
public class HomeController {

    /**
     * 현재 로그인한 회원의 닉네임을 메인 화면에 표시합니다.
     */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("nickname", userDetails.getNickname());
        return "home";
    }
}
