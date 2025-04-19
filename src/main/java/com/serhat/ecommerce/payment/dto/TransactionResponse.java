package com.serhat.ecommerce.payment.dto;

import com.serhat.ecommerce.payment.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        BigDecimal amount,
        LocalDateTime time,
        TransactionType transactionType,
        String description
) {
}
