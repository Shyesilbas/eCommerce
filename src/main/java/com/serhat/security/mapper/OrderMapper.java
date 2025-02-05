package com.serhat.security.mapper;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderItemDetails;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class
OrderMapper {
    private final AddressMapper addressMapper;

    public OrderCancellationResponse toOrderCancellationResponse(Order order, BigDecimal totalPaid) {
        return new OrderCancellationResponse(
                order.getTotalPrice(),
                order.getShippingFee(),
                order.getIsBonusPointUsed(),
                order.getBonusPointsUsed(),
                order.getTotalDiscount(),
                totalPaid,
                toOrderItemDetails(order.getOrderItems()),
                order.getStatus(),
                LocalDateTime.now(),
                "Refund processed immediately."
        );
    }

    public List<OrderItemDetails> toOrderItemDetails(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> OrderItemDetails.builder()
                        .productCode(item.getProduct().getProductCode())
                        .productName(item.getProduct().getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .brand(item.getProduct().getBrand())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
    }

    public OrderResponse toOrderResponse(Order order) {
        AddressDto shippingAddress = addressMapper.toAddressDto(order.getShippingAddressId());
        List<OrderItemDetails> orderItems = toOrderItemDetails(order.getOrderItems());

        BigDecimal cartTotalPrice = order.getTotalPrice();
        BigDecimal totalBeforeDiscount = cartTotalPrice.add(order.getShippingFee());
        BigDecimal saved = totalBeforeDiscount.subtract(order.getTotalPaid());

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderDate(),
                order.getStatus(),
                shippingAddress,
                order.getPaymentMethod(),
                order.getTotalPrice(),
                order.getShippingFee(),
                totalBeforeDiscount,
                order.getTotalDiscount(),
                order.getBonusPointsUsed(),
                order.getTotalPaid(),
                saved,
                order.getNotes(),
                orderItems,
                order.getBonusWon()
        );
    }
}
