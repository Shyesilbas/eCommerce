package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositSuccessfulResponse(
        BigDecimal depositAmount,
        BigDecimal walletBalance,
        LocalDateTime now,
        TransactionType transactionType
) {
}
