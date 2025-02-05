package com.serhat.security.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddedToCardResponse(
        String productName,
        String brand,
        String productCode,
        BigDecimal price,
        boolean isReturnable,
        String message
) {
}
