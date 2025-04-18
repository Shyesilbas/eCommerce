package com.serhat.ecommerce.dto.response;

import java.math.BigDecimal;

public record ProductPriceUpdate(
        String productName,
        String productCode,
        BigDecimal newPrice
) {
}
