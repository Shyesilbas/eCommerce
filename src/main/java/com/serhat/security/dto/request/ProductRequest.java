package com.serhat.security.dto.request;


import com.serhat.security.entity.enums.Category;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String originOfCountry,
        String productCode,
        String description,
        BigDecimal price,
        String brand,
        BigDecimal averageRating,
        String stockStatus,
        String color,
        int quantity,
        String category
) {

}
