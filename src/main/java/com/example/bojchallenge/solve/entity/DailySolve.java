package com.example.bojchallenge.solve.entity;

import com.example.bojchallenge.global.common.BaseTimeEntity;
import com.example.bojchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_solve", uniqueConstraints = {
    @UniqueConstraint(name = "uk_daily_solve_user_date", columnNames = {"user_id", "solve_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailySolve extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "solve_date", nullable = false)
    private LocalDate date;

    @Column(name = "solved_count", nullable = false)
    private int solvedCount;

    @Column(name = "problem_numbers", columnDefinition = "TEXT")
    private String problemNumbers;

    public void updateSolve(int solvedCount, String problemNumbers) {
        this.solvedCount = solvedCount;
        this.problemNumbers = problemNumbers;
    }
}
