package com.example.bojchallenge.group.repository;

import com.example.bojchallenge.group.entity.Punishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PunishmentRepository extends JpaRepository<Punishment, Long> {

    Optional<Punishment> findByGroupIdAndUserIdAndDate(Long groupId, Long userId, LocalDate date);

    List<Punishment> findByGroupIdAndDate(Long groupId, LocalDate date);
}
