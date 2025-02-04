package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record BonusUsageResult(
        BigDecimal updatedTotalPrice,
        BigDecimal bonusPointsUsed

) {
}
