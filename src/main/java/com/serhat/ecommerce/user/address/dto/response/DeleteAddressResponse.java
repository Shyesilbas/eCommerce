package com.serhat.ecommerce.user.address.dto.response;

import java.time.LocalDateTime;

public record DeleteAddressResponse(
        Long addressId,
        String message,
        LocalDateTime time
) {
}
