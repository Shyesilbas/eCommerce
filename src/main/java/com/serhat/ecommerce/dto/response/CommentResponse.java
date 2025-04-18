package com.serhat.ecommerce.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long commentId,
        Long userId,
        String username,
        Long productId,
        String productName,
        String content,
        LocalDateTime createdAt,
        int rating
) {
}
