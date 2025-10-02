package com.example.bojchallenge.group.repository;

import com.example.bojchallenge.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByUserId(Long userId);

    List<GroupMember> findByGroupId(Long groupId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    long countByGroupId(Long groupId);
}
