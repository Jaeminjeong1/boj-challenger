package com.example.bojchallenge.group.entity;

import com.example.bojchallenge.global.common.BaseTimeEntity;
import com.example.bojchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "punishments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_punishment_group_user_date", columnNames = {"group_id", "user_id", "punish_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Punishment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "punish_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean punished;

    public void updatePunished(boolean punished) {
        this.punished = punished;
    }
}
