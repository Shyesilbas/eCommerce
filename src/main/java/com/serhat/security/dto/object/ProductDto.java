package com.serhat.security.dto.object;

import com.serhat.security.entity.enums.Category;
import com.serhat.security.entity.enums.StockStatus;
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
        Category category
) {



}
