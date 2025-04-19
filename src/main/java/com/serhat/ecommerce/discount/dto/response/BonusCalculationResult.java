package com.serhat.ecommerce.discount.dto.response;

import java.math.BigDecimal;

public record BonusCalculationResult(
        BigDecimal finalPrice,
        BigDecimal bonusPointsEarned,
        BigDecimal bonusPointsUsed
) {
}
