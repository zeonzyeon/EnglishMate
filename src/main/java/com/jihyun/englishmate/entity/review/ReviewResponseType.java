package com.jihyun.englishmate.entity.review;

/**
 * 플래시카드 복습 응답 유형입니다.
 */
public enum ReviewResponseType {
    REMEMBERED("외웠어요"),
    DIFFICULT("다시 볼래요");

    private final String label;

    ReviewResponseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
