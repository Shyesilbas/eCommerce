package com.serhat.security.dto.response;

import lombok.Builder;

@Builder
public record ProductResponse(
        long productId,
        String name,
        String productCode,
        String message
) {}
