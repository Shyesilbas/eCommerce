package com.serhat.ecommerce.product.dto;

import com.serhat.ecommerce.product.enums.Category;
import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record CardProductDto(
        Long productId,
        String productCode,
        String name,
        BigDecimal price,
        String description,
        String brand,
        Category category,
        int quantity
) {
}
