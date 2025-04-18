package com.serhat.ecommerce.dto.response;

import java.math.BigDecimal;

public record WalletCreatedResponse(
        Long walletId,
        String walletName,
        BigDecimal balance,
        String description
) {
}
