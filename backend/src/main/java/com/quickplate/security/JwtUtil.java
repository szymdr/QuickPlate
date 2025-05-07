package com.quickplate.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret:SecretKeyToGenJWTs}")
    private String secret;
    @Value("${jwt.expiration:3600000}")
    private long expirationMs;

    public String generateToken(UUID userId, String role) {
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }
}