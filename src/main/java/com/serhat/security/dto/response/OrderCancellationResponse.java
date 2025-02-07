package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCancellationResponse(
        BigDecimal orderFee,
        BigDecimal shippingFee,
        Boolean isBonusPointsUsed,
        BigDecimal bonusPointsUsed,
        BigDecimal totalDiscount,
        BigDecimal giftCardAmount,
        BigDecimal refundFee,
        List<OrderItemDetails> orderItems,
        OrderStatus status,
        LocalDateTime cancellationDate,
        String message
) {
}
