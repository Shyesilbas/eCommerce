package com.serhat.ecommerce.dto.object;

import com.serhat.ecommerce.enums.Category;

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
