package com.serhat.security.dto.response;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long orderId,
        BigDecimal totalPrice,
        BigDecimal bonusWon,
        LocalDateTime orderDate,
        OrderStatus status,
        AddressDto shippingAddress,
        PaymentMethod paymentMethod,
        String notes,
        int totalQuantity,
        List<OrderItemDetails> orderItems,
        BigDecimal shippingFee,
        BigDecimal totalPaid,
        Long discountId,
        BigDecimal discountRate,
        BigDecimal discountAmount
) {
}
