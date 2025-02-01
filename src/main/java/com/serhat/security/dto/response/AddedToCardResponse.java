package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record AddedToCardResponse(
        String productName,
        String brand,
        String productCode,
        BigDecimal price
) {
}
