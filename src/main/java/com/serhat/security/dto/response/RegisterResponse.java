package com.serhat.security.dto.response;

import java.time.LocalDateTime;

public record RegisterResponse(
        String message,
        String name,
        String email,
        LocalDateTime time
) {
}
