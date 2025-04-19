package com.serhat.ecommerce.discount.dto.response;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;

import java.math.BigDecimal;

public record DiscountCalculationResult(
        BigDecimal finalPrice,
        BigDecimal discountAmount,
        DiscountCode appliedDiscountCode
) {
}
