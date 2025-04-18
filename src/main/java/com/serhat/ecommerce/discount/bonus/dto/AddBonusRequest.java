package com.serhat.ecommerce.discount.bonus.dto;

import java.math.BigDecimal;

public record AddBonusRequest(
        BigDecimal amount
) {
}
