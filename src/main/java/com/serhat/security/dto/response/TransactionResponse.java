package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        BigDecimal amount,
        LocalDateTime time,
        TransactionType transactionType,
        String description
) {
}
