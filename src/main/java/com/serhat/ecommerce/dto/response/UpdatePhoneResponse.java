package com.serhat.ecommerce.dto.response;

import java.time.LocalDateTime;

public record UpdatePhoneResponse(
        String message,
        String newPhone,
        LocalDateTime time
) {
}
