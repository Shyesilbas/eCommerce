package com.serhat.ecommerce.product.dto;

import java.math.BigDecimal;

public record ProductPriceUpdate(
        String productName,
        String productCode,
        BigDecimal newPrice
) {
}
