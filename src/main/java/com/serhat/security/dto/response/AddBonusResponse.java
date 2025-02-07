package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record AddBonusResponse(
        String message,
        BigDecimal addedAmount,
        BigDecimal currentPoints,
        BigDecimal totalBonusWon
) {
}
