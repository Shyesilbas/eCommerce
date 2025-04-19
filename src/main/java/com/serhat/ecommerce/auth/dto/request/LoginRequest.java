package com.serhat.ecommerce.auth.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
