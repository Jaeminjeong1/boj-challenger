package com.example.bojchallenge.user.service;

import com.example.bojchallenge.global.common.BadRequestException;
import com.example.bojchallenge.global.common.NotFoundException;
import com.example.bojchallenge.user.dto.DashboardResponse;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(String email, String passwordHash, String bojHandle) {
        validateSignup(email, bojHandle);
        User user = User.builder()
            .email(email)
            .passwordHash(passwordHash)
            .bojHandle(bojHandle)
            .nickname(bojHandle)
            .currentStreak(0)
            .build();
        return userRepository.save(user);
    }

    private void validateSignup(String email, String bojHandle) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByBojHandle(bojHandle)) {
            throw new BadRequestException("Handle already registered");
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public void updateSolveStatus(User user, boolean solvedToday, LocalDate today) {
        user.updateStreak(today, solvedToday);
    }

    public DashboardResponse toDashboard(User user, List<Integer> todaySolved) {
        return new DashboardResponse(user.getBojHandle(), user.getCurrentStreak(), todaySolved, todaySolved.size());
    }
}
