package com.serhat.security.dto.response;

import java.time.LocalDateTime;

public record UpdateEmailResponse(
        String message,
        String newEmail,
        LocalDateTime time
) {
}
