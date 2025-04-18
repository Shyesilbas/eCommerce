package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;

import java.math.BigDecimal;

public record DiscountDetails(
        BigDecimal discountAmount,
        DiscountCode discountCode
) {
}
