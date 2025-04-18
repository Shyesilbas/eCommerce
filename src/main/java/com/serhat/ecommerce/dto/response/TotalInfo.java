package com.serhat.ecommerce.dto.response;

import java.math.BigDecimal;

public record TotalInfo(
        BigDecimal totalPrice,
        long totalQuantity,
        long totalItems,
        String message

) {
}
