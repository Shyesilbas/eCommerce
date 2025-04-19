package com.serhat.ecommerce.auth.dto.response;

import java.time.LocalDateTime;

public record UpdatePasswordResponse(
        String message,
        LocalDateTime time
) {
}
