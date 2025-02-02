package com.serhat.security.dto.object;

import com.serhat.security.entity.enums.Category;

import java.math.BigDecimal;

public record BestSellerProductDTO(
        String name,
        String originOfCountry,
        String productCode,
        String description,
        BigDecimal price,
        String brand,
        String color,
        Category category
) {
}
