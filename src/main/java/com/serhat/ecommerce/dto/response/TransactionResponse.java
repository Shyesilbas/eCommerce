package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        BigDecimal amount,
        LocalDateTime time,
        TransactionType transactionType,
        String description
) {
}
