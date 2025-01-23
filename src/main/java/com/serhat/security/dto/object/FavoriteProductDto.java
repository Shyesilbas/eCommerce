package com.serhat.security.dto.object;

import com.serhat.security.entity.enums.Category;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record FavoriteProductDto(
        String productCode,
        String name,
        BigDecimal price,
        String description,
        String brand,
        String color,
        Category category,
        BigDecimal averageRating,
        LocalDate favorite_since
) {
}
