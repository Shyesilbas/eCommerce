package com.serhat.ecommerce.dto.object;

import com.serhat.ecommerce.enums.Category;
import com.serhat.ecommerce.enums.StockStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(
        long productId,
        String name,
        String originOfCountry,
        String productCode,
        String description,
        BigDecimal price,
        String brand,
        BigDecimal averageRating,
        StockStatus stockStatus,
        String color,
        int quantity,
        Category category,
        boolean isReturnable
) {



}
