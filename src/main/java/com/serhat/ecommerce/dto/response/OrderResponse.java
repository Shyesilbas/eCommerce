package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.user.address.dto.AddressDto;
import com.serhat.ecommerce.enums.OrderStatus;
import com.serhat.ecommerce.enums.PaymentMethod;
import lombok.Builder;

import java.io.Serializable;
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
) implements Serializable {
}

