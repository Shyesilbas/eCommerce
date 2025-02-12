package com.serhat.security.dto.response;

import com.serhat.security.entity.DiscountCode;

import java.math.BigDecimal;

public record DiscountCalculationResult(
        BigDecimal finalPrice,
        BigDecimal discountAmount,
        DiscountCode appliedDiscountCode
) {
}
