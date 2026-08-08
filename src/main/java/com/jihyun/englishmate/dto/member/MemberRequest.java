package com.jihyun.englishmate.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Member 관련 요청 DTO를 모아둔 클래스입니다.
 */
public class MemberRequest {

    /**
     * 회원가입 화면에서 입력한 값을 전달합니다.
     */
    public record Signup(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "올바른 이메일 형식으로 입력해주세요.")
            String email,

            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해주세요.")
            String password,

            @NotBlank(message = "비밀번호 확인을 입력해주세요.")
            @Size(min = 8, max = 30, message = "비밀번호 확인은 8자 이상 30자 이하로 입력해주세요.")
            String confirmPassword,

            @NotBlank(message = "닉네임을 입력해주세요.")
            @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
            String nickname
    ) {
        public boolean passwordMismatch() {
            return password != null && confirmPassword != null && !password.equals(confirmPassword);
        }
    }
}
