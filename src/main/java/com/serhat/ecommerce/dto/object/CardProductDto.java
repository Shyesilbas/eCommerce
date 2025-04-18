package com.serhat.ecommerce.dto.object;

import com.serhat.ecommerce.enums.Category;
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
