package com.serhat.ecommerce.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceHistoryResponse(
        String productName,
        Long productId,
        BigDecimal oldPrice,
        BigDecimal newPrice,
        double changePercentage,
        double totalChangePercentage,
        LocalDateTime changeDate
) {
}
