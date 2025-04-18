package com.serhat.ecommerce.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
