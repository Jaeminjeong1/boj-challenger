package com.example.bojchallenge.global.security;

import com.example.bojchallenge.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public record SecurityUserContext(Long userId, String email, String nickname) {

    public static SecurityUserContext from(User user) {
        return new SecurityUserContext(user.getId(), user.getEmail(), user.getNickname());
    }

    public Collection<? extends GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
