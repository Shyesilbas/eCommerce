package com.serhat.ecommerce.discount.dto.response;

import java.math.BigDecimal;

public record BonusPointInformation(
        BigDecimal totalBonusWon,
        BigDecimal currentBonusPoints
) {
}
