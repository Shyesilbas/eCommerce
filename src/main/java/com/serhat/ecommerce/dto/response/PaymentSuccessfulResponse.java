package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSuccessfulResponse(
        BigDecimal amount,
        BigDecimal walletBalance,
        LocalDateTime time,
        TransactionType transactionType
) {
}
