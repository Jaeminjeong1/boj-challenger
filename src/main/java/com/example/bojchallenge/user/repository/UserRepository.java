package com.example.bojchallenge.user.repository;

import com.example.bojchallenge.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByBojHandle(String bojHandle);

    boolean existsByEmail(String email);

    boolean existsByBojHandle(String bojHandle);
}
