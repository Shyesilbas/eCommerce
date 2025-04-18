package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.DiscountRate;

public record ExpiredDiscountResponse(
        Long discountId,
        DiscountRate discountRate,
        String code
) {
}
