package com.example.bojchallenge.solve.repository;

import com.example.bojchallenge.solve.entity.SolveSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SolveSnapshotRepository extends JpaRepository<SolveSnapshot, Long> {

    Optional<SolveSnapshot> findByUserIdAndDate(Long userId, LocalDate date);

    List<SolveSnapshot> findByDate(LocalDate date);
}
