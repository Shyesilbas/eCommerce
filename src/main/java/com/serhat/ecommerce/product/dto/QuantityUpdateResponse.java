package com.serhat.ecommerce.product.dto;

public record QuantityUpdateResponse(
        String productName,
        String productCode,
        int quantity
) {
}
