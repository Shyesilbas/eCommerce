package com.serhat.ecommerce.product.dto;

import lombok.Builder;

@Builder
public record ProductResponse(
        long productId,
        String name,
        String productCode,
        String message
) {}
