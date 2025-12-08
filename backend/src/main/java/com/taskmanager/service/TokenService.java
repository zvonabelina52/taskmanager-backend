package com.taskmanager.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.HashSet;

@ApplicationScoped
public class TokenService {

    public String generateToken(String username, String email) {
        return Jwt.issuer("https://taskmanager.com")
                .upn(username)
                .claim("email", email)
                .groups(new HashSet<>(java.util.Arrays.asList("user")))
                .expiresIn(Duration.ofHours(24))
                .sign();
    }
}