package com.serhat.ecommerce.sCard.dto;

import com.serhat.ecommerce.product.dto.CardProductDto;

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
