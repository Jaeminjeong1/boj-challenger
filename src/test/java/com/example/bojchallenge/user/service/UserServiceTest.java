package com.example.bojchallenge.user.service;

import com.example.bojchallenge.global.common.BadRequestException;
import com.example.bojchallenge.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerEncodesPassword() {
        User user = userService.register("test@example.com", passwordEncoder.encode("password123"), "handle123");
        assertThat(user.getPasswordHash()).isNotEqualTo("password123");
        assertThat(user.getNickname()).isEqualTo("handle123");
    }

    @Test
    void registerDuplicateEmailThrowsException() {
        userService.register("dup@example.com", passwordEncoder.encode("password123"), "handleAAA");
        assertThatThrownBy(() -> userService.register("dup@example.com", passwordEncoder.encode("password123"), "handleBBB"))
            .isInstanceOf(BadRequestException.class);
    }
}
