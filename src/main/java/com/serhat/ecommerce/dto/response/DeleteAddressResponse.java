package com.serhat.ecommerce.dto.response;

import java.time.LocalDateTime;

public record DeleteAddressResponse(
        Long addressId,
        String message,
        LocalDateTime time
) {
}
