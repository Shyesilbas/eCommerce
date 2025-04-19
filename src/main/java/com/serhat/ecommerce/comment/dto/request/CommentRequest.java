package com.serhat.ecommerce.comment.dto.request;

import lombok.Builder;

@Builder
public record CommentRequest(
        Long productId,
        Long orderId,
        String content,
        int rating
) {
}
