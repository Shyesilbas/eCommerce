package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCancellationResponse(
        BigDecimal refundFee,
        List<OrderItemDetails> orderItems,
        OrderStatus status,
        LocalDateTime cancellationDate,
        String message
) {
}
