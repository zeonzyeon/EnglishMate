package com.jihyun.englishmate.dto.member;

import com.jihyun.englishmate.entity.member.Member;
import java.time.LocalDateTime;

/**
 * Member 관련 응답 DTO를 모아둔 클래스입니다.
 */
public class MemberResponse {

    /**
     * 회원가입 완료 후 필요한 회원 정보를 전달합니다.
     */
    public record Signup(
            Long id,
            String email,
            String nickname,
            LocalDateTime createdAt
    ) {

        /**
         * Member 엔티티를 응답 DTO로 변환합니다.
         */
        public static Signup from(Member member) {
            return new Signup(
                    member.getId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getCreatedAt()
            );
        }
    }
}
