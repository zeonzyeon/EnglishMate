package com.jihyun.englishmate.entity.quiz;

import com.jihyun.englishmate.entity.word.Word;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 퀴즈의 각 문제와 사용자의 제출 결과를 저장합니다.
 */
@Entity
@Table(
        name = "quiz_question_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quiz_question_order",
                columnNames = {"quiz_attempt_id", "question_order"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizQuestionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(nullable = false)
    private int questionOrder;

    @Column(nullable = false, length = 500)
    private String questionText;

    @Column(nullable = false, length = 500)
    private String correctAnswer;

    @Column(length = 500)
    private String submittedAnswer;

    private Boolean correct;

    private LocalDateTime answeredAt;

    @ElementCollection
    @CollectionTable(
            name = "quiz_question_choices",
            joinColumns = @JoinColumn(name = "quiz_question_result_id")
    )
    @OrderColumn(name = "choice_order")
    @Column(name = "choice_value", nullable = false, length = 500)
    private List<String> choices = new ArrayList<>();

    private QuizQuestionResult(
            QuizAttempt quizAttempt,
            Word word,
            int questionOrder,
            String questionText,
            String correctAnswer,
            List<String> choices
    ) {
        this.quizAttempt = quizAttempt;
        this.word = word;
        this.questionOrder = questionOrder;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.choices = new ArrayList<>(choices);
    }

    public static QuizQuestionResult create(
            QuizAttempt quizAttempt,
            Word word,
            int questionOrder,
            String questionText,
            String correctAnswer,
            List<String> choices
    ) {
        return new QuizQuestionResult(quizAttempt, word, questionOrder, questionText, correctAnswer, choices);
    }

    /**
     * 한 문제의 제출 답안과 채점 결과를 저장합니다.
     */
    public void submit(String submittedAnswer, boolean correct) {
        this.submittedAnswer = submittedAnswer;
        this.correct = correct;
        this.answeredAt = LocalDateTime.now();
    }

    public boolean isAnswered() {
        return this.answeredAt != null;
    }
}
