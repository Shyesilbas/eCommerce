package com.serhat.ecommerce.dto.response;

public record AverageBrandRatingResponse(
        double averageRating,

        String brand
) {
}
