package com.serhat.security.dto.response;

import java.math.BigDecimal;

public record TotalInfo(
        BigDecimal totalPrice,
        long totalQuantity,
        long totalItems,
        String message

) {
}
