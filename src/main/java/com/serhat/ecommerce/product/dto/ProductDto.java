package com.serhat.ecommerce.product.dto;

import com.serhat.ecommerce.product.enums.Category;
import com.serhat.ecommerce.product.enums.StockStatus;
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
