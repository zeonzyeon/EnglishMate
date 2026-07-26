package com.jihyun.englishmate.exception.member;

/**
 * 이미 가입된 이메일로 회원가입을 시도할 때 발생하는 예외입니다.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
