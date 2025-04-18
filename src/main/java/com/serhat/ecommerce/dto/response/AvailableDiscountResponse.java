package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.DiscountRate;

public record AvailableDiscountResponse(
        Long discountId,
        DiscountRate discountRate,
        String code
) {
}
