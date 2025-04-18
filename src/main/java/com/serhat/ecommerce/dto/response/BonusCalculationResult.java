package com.serhat.ecommerce.dto.response;

import java.math.BigDecimal;

public record BonusCalculationResult(
        BigDecimal finalPrice,
        BigDecimal bonusPointsEarned,
        BigDecimal bonusPointsUsed
) {
}
