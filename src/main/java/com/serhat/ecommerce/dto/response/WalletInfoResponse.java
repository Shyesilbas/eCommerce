package com.serhat.ecommerce.dto.response;

import java.math.BigDecimal;

public record WalletInfoResponse(
        String description,
        String walletName,
        BigDecimal walletLimit,
        BigDecimal balance
) {
}
