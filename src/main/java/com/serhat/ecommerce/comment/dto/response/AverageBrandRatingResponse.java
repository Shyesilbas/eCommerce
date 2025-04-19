package com.serhat.ecommerce.comment.dto.response;

public record AverageBrandRatingResponse(
        double averageRating,

        String brand
) {
}
