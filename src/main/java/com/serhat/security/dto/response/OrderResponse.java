package com.serhat.security.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long orderId,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String status,
        Long shippingAddressId,
        String paymentMethod,
        String notes,
        int totalQuantity,
        List<OrderItemDetails> orderItems
) {
}
