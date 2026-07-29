package com.jihyun.englishmate.entity.quiz;

/**
 * 퀴즈 출제 유형을 관리합니다. 추후 문맥 빈칸 유형을 이 enum에 추가할 수 있습니다.
 */
public enum QuizType {

    WORD_TO_MEANING_MULTIPLE_CHOICE("영어 단어 → 뜻 고르기", true, true),
    MEANING_TO_WORD_MULTIPLE_CHOICE("뜻 → 영어 단어 고르기", true, false),
    WORD_TO_MEANING_WRITTEN("영어 단어 → 뜻 직접 입력", false, true),
    MEANING_TO_WORD_WRITTEN("뜻 → 영어 단어 직접 입력", false, false);

    private final String label;
    private final boolean multipleChoice;
    private final boolean wordQuestion;

    QuizType(String label, boolean multipleChoice, boolean wordQuestion) {
        this.label = label;
        this.multipleChoice = multipleChoice;
        this.wordQuestion = wordQuestion;
    }

    public String getLabel() {
        return label;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public boolean isWritten() {
        return !multipleChoice;
    }

    public boolean isWordQuestion() {
        return wordQuestion;
    }
}
