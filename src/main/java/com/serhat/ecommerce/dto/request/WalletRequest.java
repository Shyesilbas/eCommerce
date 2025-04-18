package com.serhat.ecommerce.dto.request;

import java.math.BigDecimal;

public record WalletRequest(
        String walletName,
        BigDecimal limit,
        String description,
        String walletPin
) {
}
