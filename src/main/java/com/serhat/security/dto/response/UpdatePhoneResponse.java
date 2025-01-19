package com.serhat.security.dto.response;

import java.time.LocalDateTime;

public record UpdatePhoneResponse(
        String message,
        String newPhone,
        LocalDateTime time
) {
}
