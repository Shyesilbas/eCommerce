package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.dto.object.CardProductDto;

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
