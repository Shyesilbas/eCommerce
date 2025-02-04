package com.serhat.security.dto.response;

import com.serhat.security.entity.DiscountCode;

import java.math.BigDecimal;

public record PriceDetails(
        BigDecimal totalPrice,
        BigDecimal originalTotalPrice,
        BigDecimal shippingFee,
        BigDecimal bonusPoints,
        BigDecimal discountAmount,
        BigDecimal finalPrice,
        DiscountCode discountCode,
        BigDecimal bonusPointsUsed
) {
}
