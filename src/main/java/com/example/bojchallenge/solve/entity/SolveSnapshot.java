package com.example.bojchallenge.solve.entity;

import com.example.bojchallenge.global.common.BaseTimeEntity;
import com.example.bojchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "solve_snapshot", uniqueConstraints = {
    @UniqueConstraint(name = "uk_snapshot_user_date", columnNames = {"user_id", "snapshot_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SolveSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate date;

    @Column(name = "total_solved", nullable = false)
    private int totalSolved;

    public void updateTotalSolved(int totalSolved) {
        this.totalSolved = totalSolved;
    }
}
