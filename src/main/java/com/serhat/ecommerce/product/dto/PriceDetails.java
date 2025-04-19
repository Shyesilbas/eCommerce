package com.serhat.ecommerce.product.dto;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PriceDetails(
        BigDecimal totalPrice,
        BigDecimal originalPrice,
        BigDecimal shippingFee,
        BigDecimal bonusPoints,
        BigDecimal discountAmount,
        BigDecimal finalPrice,
        DiscountCode discountCode,
        BigDecimal bonusPointsUsed,
        BigDecimal totalSaved
) {
}
