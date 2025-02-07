package com.serhat.security.dto.response;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long orderId,
        LocalDateTime orderDate,
        OrderStatus status,
        AddressDto shippingAddress,
        PaymentMethod paymentMethod,

        BigDecimal cartTotalPrice,
        BigDecimal shippingFee,
        BigDecimal totalBeforeDiscount,

        BigDecimal discountAmount,
        BigDecimal bonusPointsUsed,
        BigDecimal giftCardAmount,
        BigDecimal totalPaid,
        BigDecimal saved,

        String notes,
        List<OrderItemDetails> orderItems,
        BigDecimal bonusWon,
        String discountMessage,
        boolean isOrderReturnable
) {
}

