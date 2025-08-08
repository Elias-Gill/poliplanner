package com.elias_gill.poliplanner.security;

import org.springframework.stereotype.Component;

@Component
public class TokenValidator {
    // TODO: hacer mas robusto
    private final String expectedKey = System.getenv("UPDATE_KEY");

    public Boolean isValid(String authHeader) {
        if (authHeader == null || !authHeader.trim().equals("Bearer " + expectedKey)) {
            return false;
        }

        return true;
    }
}
