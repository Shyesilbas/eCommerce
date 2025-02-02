package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record ProductPriceUpdate(
        String productName,
        String productCode,
        BigDecimal newPrice
) {
}
