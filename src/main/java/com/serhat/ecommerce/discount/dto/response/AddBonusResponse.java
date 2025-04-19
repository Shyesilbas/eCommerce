package com.serhat.ecommerce.discount.dto.response;

import java.math.BigDecimal;

public record AddBonusResponse(
        String message,
        BigDecimal addedAmount,
        BigDecimal currentPoints,
        BigDecimal totalBonusWon
) {
}
