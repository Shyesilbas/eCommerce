package com.serhat.security.dto.response;

import com.serhat.security.entity.DiscountCode;

import java.math.BigDecimal;

public record DiscountDetails(
        BigDecimal discountAmount,
        DiscountCode discountCode
) {
}
