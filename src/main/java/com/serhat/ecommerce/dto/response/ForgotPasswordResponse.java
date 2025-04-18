package com.serhat.ecommerce.dto.response;

import java.time.LocalDateTime;

public record ForgotPasswordResponse(
        String message,
        LocalDateTime now
) {
}
