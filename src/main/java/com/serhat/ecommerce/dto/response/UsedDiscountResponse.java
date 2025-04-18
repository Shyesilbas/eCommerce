package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.DiscountRate;

import java.math.BigDecimal;

public record UsedDiscountResponse(
        Long discountId,
        Long orderId,
        BigDecimal orderFee,
        DiscountRate discountRate,
        BigDecimal discountAmount,
        String code
) {}

