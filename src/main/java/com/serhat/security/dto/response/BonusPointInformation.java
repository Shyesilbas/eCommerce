package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record BonusPointInformation(
        BigDecimal totalBonusWon,
        BigDecimal currentBonusPoints
) {
}
