package com.example.bojchallenge.user.entity;

import com.example.bojchallenge.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_user_boj_handle", columnNames = "boj_handle")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, name = "password_hash", length = 120)
    private String passwordHash;

    @Column(nullable = false, name = "boj_handle", length = 40)
    private String bojHandle;

    @Column(nullable = false, length = 60)
    private String nickname;

    @Column(name = "last_solved_at")
    private LocalDate lastSolvedAt;

    @Column(nullable = false)
    private int currentStreak;

    public void updateStreak(LocalDate date, boolean solvedToday) {
        if (solvedToday) {
            if (lastSolvedAt != null && lastSolvedAt.plusDays(1).isEqual(date)) {
                currentStreak += 1;
            } else if (date.equals(lastSolvedAt)) {
                // solved multiple times on same day, keep streak
            } else {
                currentStreak = 1;
            }
            lastSolvedAt = date;
        } else if (lastSolvedAt == null || !lastSolvedAt.isEqual(date)) {
            currentStreak = 0;
        }
    }

    public void mirrorNickname() {
        this.nickname = this.bojHandle;
    }
}
