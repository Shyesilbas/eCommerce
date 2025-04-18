package com.serhat.ecommerce.dto.response;

import java.time.LocalDateTime;

public record UpdateEmailResponse(
        String message,
        String newEmail,
        LocalDateTime time
) {
}
