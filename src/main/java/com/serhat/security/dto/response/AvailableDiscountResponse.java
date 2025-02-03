package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.DiscountRate;

public record AvailableDiscountResponse(
        Long discountId,
        DiscountRate discountRate,
        String code
) {
}
