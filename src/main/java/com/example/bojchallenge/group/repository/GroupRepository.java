package com.example.bojchallenge.group.repository;

import com.example.bojchallenge.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByPin4(String pin4);

    boolean existsByName(String name);

    boolean existsByPin4(String pin4);
}
