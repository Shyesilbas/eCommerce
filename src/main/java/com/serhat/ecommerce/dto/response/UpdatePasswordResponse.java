package com.serhat.ecommerce.dto.response;

import java.time.LocalDateTime;

public record UpdatePasswordResponse(
        String message,
        LocalDateTime time
) {
}
