package com.jihyun.englishmate.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 인증 관련 화면을 반환합니다.
 */
@Controller
public class AuthController {

    /**
     * Spring Security Form Login에서 사용할 로그인 화면입니다.
     */
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
