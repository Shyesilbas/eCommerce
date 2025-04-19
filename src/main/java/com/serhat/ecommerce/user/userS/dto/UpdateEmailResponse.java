package com.serhat.ecommerce.user.userS.dto;

import java.time.LocalDateTime;

public record UpdateEmailResponse(
        String message,
        String newEmail,
        LocalDateTime time
) {
}
