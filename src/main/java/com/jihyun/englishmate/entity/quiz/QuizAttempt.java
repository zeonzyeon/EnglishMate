package com.jihyun.englishmate.entity.quiz;

import com.jihyun.englishmate.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한 번 시작한 퀴즈 진행 정보를 저장합니다.
 */
@Entity
@Table(name = "quiz_attempts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private QuizType quizType;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int correctCount;

    @Column(nullable = false)
    private int wrongCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuizAttemptStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private QuizAttempt(Member member, QuizType quizType, int totalQuestions) {
        this.member = member;
        this.quizType = quizType;
        this.totalQuestions = totalQuestions;
        this.correctCount = 0;
        this.wrongCount = 0;
        this.status = QuizAttemptStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public static QuizAttempt start(Member member, QuizType quizType, int totalQuestions) {
        return new QuizAttempt(member, quizType, totalQuestions);
    }

    /**
     * 제출된 문제의 채점 결과를 누적합니다.
     */
    public void recordAnswer(boolean correct) {
        if (correct) {
            this.correctCount++;
            return;
        }
        this.wrongCount++;
    }

    /**
     * 모든 문제가 제출되면 퀴즈를 완료 상태로 변경합니다.
     */
    public void complete() {
        this.status = QuizAttemptStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return this.status == QuizAttemptStatus.COMPLETED;
    }
}
