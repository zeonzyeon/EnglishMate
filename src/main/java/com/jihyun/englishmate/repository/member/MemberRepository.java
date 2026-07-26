package com.jihyun.englishmate.repository.member;

import com.jihyun.englishmate.entity.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Member 엔티티의 데이터 접근을 담당합니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일 중복 검사를 위해 이메일 존재 여부를 조회합니다.
     */
    boolean existsByEmail(String email);

    /**
     * 이후 로그인 기능에서 사용할 수 있도록 이메일 기반 조회를 제공합니다.
     */
    Optional<Member> findByEmail(String email);
}
