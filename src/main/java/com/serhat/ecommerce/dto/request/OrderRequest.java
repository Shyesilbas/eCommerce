package com.serhat.ecommerce.dto.request;

import com.serhat.ecommerce.enums.PaymentMethod;
import lombok.Builder;

@Builder
public record OrderRequest(
        Long shippingAddressId,
        Long discountId,
        Long giftCardId,
        PaymentMethod paymentMethod,
        String notes,
        Boolean useBonus
) {
}
