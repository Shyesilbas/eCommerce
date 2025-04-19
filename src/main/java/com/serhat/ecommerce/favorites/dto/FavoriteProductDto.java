package com.serhat.ecommerce.favorites.dto;

import com.serhat.ecommerce.product.enums.Category;
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
