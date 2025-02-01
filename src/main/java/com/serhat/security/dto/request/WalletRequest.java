package com.serhat.security.dto.request;

import java.math.BigDecimal;

public record WalletRequest(
        String walletName,
        BigDecimal limit,
        String description,
        String walletPin
) {
}
