package com.serhat.ecommerce.wallet.dto;

import java.math.BigDecimal;

public record WalletRequest(
        String walletName,
        BigDecimal limit,
        String description,
        String walletPin
) {
}
