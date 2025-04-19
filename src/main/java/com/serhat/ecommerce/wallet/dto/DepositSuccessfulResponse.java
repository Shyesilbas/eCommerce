package com.serhat.ecommerce.wallet.dto;

import com.serhat.ecommerce.payment.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositSuccessfulResponse(
        BigDecimal depositAmount,
        BigDecimal walletBalance,
        LocalDateTime now,
        TransactionType transactionType
) {
}
