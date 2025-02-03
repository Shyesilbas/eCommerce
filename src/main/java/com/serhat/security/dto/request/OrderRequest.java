package com.serhat.security.dto.request;

import com.serhat.security.entity.enums.PaymentMethod;
import lombok.Builder;

@Builder
public record OrderRequest(
        Long shippingAddressId,
        Long discountId,
        PaymentMethod paymentMethod,
        String notes
) {
}
