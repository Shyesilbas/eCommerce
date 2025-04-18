package com.serhat.ecommerce.dto.object;

import com.serhat.ecommerce.enums.Category;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record FavoriteProductDto(
        Long productId,
        String productCode,
        String name,
        BigDecimal price,
        String description,
        String brand,
        String color,
        Category category,
        BigDecimal averageRating,
        LocalDate favorite_since,
        boolean isFavorite
) {
}
