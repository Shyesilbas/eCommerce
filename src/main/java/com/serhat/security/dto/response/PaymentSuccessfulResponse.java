package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSuccessfulResponse(
        BigDecimal amount,
        BigDecimal walletBalance,
        LocalDateTime time,
        TransactionType transactionType
) {
}
