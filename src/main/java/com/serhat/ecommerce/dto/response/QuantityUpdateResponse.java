package com.serhat.ecommerce.dto.response;

public record QuantityUpdateResponse(
        String productName,
        String productCode,
        int quantity
) {
}
