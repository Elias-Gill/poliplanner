package com.elias_gill.poliplanner.security;

import org.springframework.stereotype.Component;

@Component
public class TokenValidator {

    private final String expectedKey = System.getenv("UPDATE_KEY");

    public void validate(String authHeader) {
        if (authHeader == null || !authHeader.trim().equals("Bearer " + expectedKey)) {
            throw new SecurityException("Token inválido");
        }
    }
}
