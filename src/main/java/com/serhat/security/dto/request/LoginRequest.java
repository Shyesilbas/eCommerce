package com.serhat.security.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
