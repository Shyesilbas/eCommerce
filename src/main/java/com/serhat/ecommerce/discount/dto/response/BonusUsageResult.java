package com.serhat.ecommerce.discount.dto.response;

import java.math.BigDecimal;

public record BonusUsageResult(
        BigDecimal updatedTotalPrice,
        BigDecimal bonusPointsUsed

) {
}
