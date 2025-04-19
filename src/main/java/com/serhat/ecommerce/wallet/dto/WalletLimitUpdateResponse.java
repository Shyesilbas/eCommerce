package com.serhat.ecommerce.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletLimitUpdateResponse(
        String message,
        BigDecimal newLimit,
        LocalDateTime time
) {
}
