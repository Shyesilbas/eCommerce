package com.serhat.ecommerce.dto.response;

import java.math.BigDecimal;

public record BonusUsageResult(
        BigDecimal updatedTotalPrice,
        BigDecimal bonusPointsUsed

) {
}
