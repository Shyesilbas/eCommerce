package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record WalletInfoResponse(
        String description,
        String walletName,
        BigDecimal walletLimit,
        BigDecimal balance
) {
}
