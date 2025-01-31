package com.serhat.security.dto.response;

import java.time.LocalDateTime;

public record UpdateAddressResponse(
        String message,
        Long addressId,
        LocalDateTime updatedAt,
        String description
) {
}
