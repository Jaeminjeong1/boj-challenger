package com.example.bojchallenge.auth.service;

import com.example.bojchallenge.auth.dto.AuthResponse;
import com.example.bojchallenge.auth.dto.LoginRequest;
import com.example.bojchallenge.auth.dto.SignupRequest;
import com.example.bojchallenge.global.common.UnauthorizedException;
import com.example.bojchallenge.global.security.JwtProvider;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(SignupRequest request) {
        String encoded = passwordEncoder.encode(request.password());
        userService.register(request.email(), encoded, request.bojHandle());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        String token = jwtProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token);
    }
}
