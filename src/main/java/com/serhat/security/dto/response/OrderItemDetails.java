package com.serhat.security.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemDetails(
        String productCode,
        String productName,
        BigDecimal price,
        Integer quantity,
        String brand,
        BigDecimal subtotal,
        boolean isReturnable

) {
}
