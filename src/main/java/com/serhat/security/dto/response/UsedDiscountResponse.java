package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.DiscountRate;

import java.math.BigDecimal;

public record UsedDiscountResponse(
        Long discountId,
        Long orderId,
        BigDecimal orderFee,
        DiscountRate discountRate,
        BigDecimal discountAmount,
        String code
) {}

