package com.serhat.security.dto.object;

import com.serhat.security.entity.enums.Category;
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
        Category category
) {
}
