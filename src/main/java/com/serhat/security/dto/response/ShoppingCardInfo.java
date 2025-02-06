package com.serhat.security.dto.response;

import com.serhat.security.dto.object.CardProductDto;

import java.math.BigDecimal;
import java.util.List;

public record ShoppingCardInfo(
        BigDecimal totalPrice,
        long totalItems,
        long totalQuantity,
        String message,
        List<CardProductDto> shoppingCardItems
) {
}
