package com.serhat.ecommerce.comment.dto.response;

public record AverageRatingResponse(
        double averageRating,
        String productName,
        String brand,
        String productCode
) {
}
