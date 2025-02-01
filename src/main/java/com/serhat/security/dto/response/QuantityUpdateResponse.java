package com.serhat.security.dto.response;

public record QuantityUpdateResponse(
        String productName,
        String productCode,
        int quantity
) {
}
