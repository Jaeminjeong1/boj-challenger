package com.example.bojchallenge.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtProperties properties;
    private final Key key;

    public JwtProvider(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(properties.getExpirationMs());
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("email", email)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(key)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = parseClaims(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }
}
