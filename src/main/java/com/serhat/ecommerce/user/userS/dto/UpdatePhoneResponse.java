package com.serhat.ecommerce.user.userS.dto;

import java.time.LocalDateTime;

public record UpdatePhoneResponse(
        String message,
        String newPhone,
        LocalDateTime time
) {
}
