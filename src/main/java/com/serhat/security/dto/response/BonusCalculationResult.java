package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record BonusCalculationResult(
        BigDecimal finalPrice,
        BigDecimal bonusPointsEarned,
        BigDecimal bonusPointsUsed
) {
}
