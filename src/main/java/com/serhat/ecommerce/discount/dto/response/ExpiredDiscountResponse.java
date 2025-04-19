package com.serhat.ecommerce.discount.dto.response;

import com.serhat.ecommerce.discount.enums.DiscountRate;

public record ExpiredDiscountResponse(
        Long discountId,
        DiscountRate discountRate,
        String code
) {
}
