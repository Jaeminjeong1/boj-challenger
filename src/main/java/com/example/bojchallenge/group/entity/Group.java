package com.example.bojchallenge.group.entity;

import com.example.bojchallenge.global.common.BaseTimeEntity;
import com.example.bojchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups", uniqueConstraints = {
    @UniqueConstraint(name = "uk_group_name", columnNames = "name"),
    @UniqueConstraint(name = "uk_group_pin4", columnNames = "pin4")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Group extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 4)
    private String pin4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public void changeName(String name) {
        this.name = name;
    }

    public void changePin4(String pin4) {
        this.pin4 = pin4;
    }
}
