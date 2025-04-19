package com.serhat.ecommerce.wallet.dto;

import java.math.BigDecimal;

public record WalletInfoResponse(
        String description,
        String walletName,
        BigDecimal walletLimit,
        BigDecimal balance
) {
}
