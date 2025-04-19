package com.serhat.ecommerce.discount.dto.response;

import com.serhat.ecommerce.discount.enums.DiscountRate;

public record AvailableDiscountResponse(
        Long discountId,
        DiscountRate discountRate,
        String code
) {
}
