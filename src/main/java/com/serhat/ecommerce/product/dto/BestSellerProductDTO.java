package com.serhat.ecommerce.product.dto;

import com.serhat.ecommerce.product.enums.Category;

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
