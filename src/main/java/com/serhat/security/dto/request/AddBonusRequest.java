package com.serhat.security.dto.request;

import java.math.BigDecimal;

public record AddBonusRequest(
        BigDecimal amount
) {
}
