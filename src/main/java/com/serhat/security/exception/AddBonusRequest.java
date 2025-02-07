package com.serhat.security.exception;

import java.math.BigDecimal;

public record AddBonusRequest(
        BigDecimal amount
) {
}
