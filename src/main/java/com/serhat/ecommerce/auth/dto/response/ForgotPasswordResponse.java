package com.serhat.ecommerce.auth.dto.response;

import java.time.LocalDateTime;

public record ForgotPasswordResponse(
        String message,
        LocalDateTime now
) {
}
