package com.serhat.security.dto.response;

public record UserResponse(
        String email,
        String username,
        String password,
        String role
) {
}
