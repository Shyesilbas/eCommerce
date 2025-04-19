package com.serhat.ecommerce.wallet.dto;

import java.math.BigDecimal;

public record WalletCreatedResponse(
        Long walletId,
        String walletName,
        BigDecimal balance,
        String description
) {
}
