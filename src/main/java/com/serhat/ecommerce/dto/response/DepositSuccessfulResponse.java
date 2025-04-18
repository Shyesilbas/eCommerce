package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositSuccessfulResponse(
        BigDecimal depositAmount,
        BigDecimal walletBalance,
        LocalDateTime now,
        TransactionType transactionType
) {
}
