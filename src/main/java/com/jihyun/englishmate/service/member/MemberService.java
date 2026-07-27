package com.jihyun.englishmate.service.member;

import com.jihyun.englishmate.dto.member.MemberRequest;
import com.jihyun.englishmate.dto.member.MemberResponse;
import com.jihyun.englishmate.entity.member.Member;
import com.jihyun.englishmate.exception.member.DuplicateEmailException;
import com.jihyun.englishmate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 도메인의 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 중복 여부를 확인한 뒤 신규 회원을 저장합니다.
     */
    @Transactional
    public MemberResponse.Signup signup(MemberRequest.Signup request) {
        validateDuplicateEmail(request.email());

        Member member = Member.createMember(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()
        );

        Member savedMember = memberRepository.save(member);
        return MemberResponse.Signup.from(savedMember);
    }

    /**
     * 이미 가입된 이메일이면 회원가입을 중단합니다.
     */
    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }
    }
}
