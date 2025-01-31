package com.serhat.security.dto.request;

import lombok.Builder;

@Builder
public record CommentRequest(
        Long productId,
        Long orderId,
        String content,
        int rating
) {
}
