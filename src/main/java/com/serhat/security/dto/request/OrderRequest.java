package com.serhat.security.dto.request;

import lombok.Builder;

@Builder
public record OrderRequest(
        Long shippingAddressId,
        String paymentMethod,
        String notes
) {
}
