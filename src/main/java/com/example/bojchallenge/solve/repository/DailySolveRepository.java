package com.example.bojchallenge.solve.repository;

import com.example.bojchallenge.solve.entity.DailySolve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySolveRepository extends JpaRepository<DailySolve, Long> {

    Optional<DailySolve> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailySolve> findByDate(LocalDate date);
}
