package com.example.bojchallenge.global.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    @Test
    void generateTokenAndExtractUserId() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-should-be-long-32-chars!!!");
        properties.setExpirationMs(3_600_000L);

        JwtProvider provider = new JwtProvider(properties);
        String token = provider.generateToken(42L, "user@example.com");

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.extractUserId(token)).isEqualTo(42L);
    }
}
