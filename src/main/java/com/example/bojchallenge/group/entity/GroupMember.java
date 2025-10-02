package com.example.bojchallenge.group.entity;

import com.example.bojchallenge.global.common.BaseTimeEntity;
import com.example.bojchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members", uniqueConstraints = {
    @UniqueConstraint(name = "uk_group_member", columnNames = {"group_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GroupRole role;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    public boolean isOwner() {
        return role == GroupRole.OWNER;
    }
}
