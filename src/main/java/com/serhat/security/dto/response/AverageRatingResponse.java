package com.serhat.security.dto.response;

public record AverageRatingResponse(
        double averageRating,
        String productName,
        String brand,
        String productCode
) {
}
