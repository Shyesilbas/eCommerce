package com.serhat.security.mapper;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderItemDetails;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.service.DiscountCodeService;
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
    private final DiscountCodeService discountService;

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
                        .isReturnable(item.getProduct().isReturnable())
                        .build())
                .collect(Collectors.toList());
    }

    public OrderResponse toOrderResponse(Order order) {
        AddressDto shippingAddress = addressMapper.toAddressDto(order.getShippingAddressId());
        List<OrderItemDetails> orderItems = toOrderItemDetails(order.getOrderItems());

        BigDecimal cartTotalPrice = order.getTotalPrice();
        BigDecimal totalBeforeDiscount = cartTotalPrice.add(order.getShippingFee());
        BigDecimal saved = totalBeforeDiscount.subtract(order.getTotalPaid());
        String discountMessage = (order.getTotalPaid().compareTo(discountService.getDiscountThreshold()) >= 0)
                ? "You’ve been granted a discount code!"
                : "Add more to obtain a discount code.";

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
                order.getBonusWon(),
                discountMessage,
                order.isOrderReturnable()
        );
    }

    public Order createOrderEntity(User user, OrderRequest orderRequest, PriceDetails priceDetails) {
        List<ShoppingCard> shoppingCards = user.getS_card();
        boolean isOrderReturnable = shoppingCards.stream()
                .allMatch(shoppingCard -> shoppingCard.getProduct().isReturnable());

        return Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(priceDetails.originalTotalPrice())
                .status(OrderStatus.APPROVED)
                .shippingAddressId(orderRequest.shippingAddressId())
                .paymentMethod(PaymentMethod.E_WALLET)
                .notes(orderRequest.notes())
                .updatedAt(LocalDateTime.now())
                .shippingFee(priceDetails.shippingFee())
                .bonusWon(priceDetails.bonusPoints())
                .discountCode(priceDetails.discountCode())
                .totalDiscount(priceDetails.discountAmount())
                .discountRate(priceDetails.discountCode() != null ?
                        priceDetails.discountCode().getDiscountRate() : DiscountRate.ZERO)
                .totalPaid(priceDetails.finalPrice())
                .isBonusPointUsed(priceDetails.bonusPointsUsed().compareTo(BigDecimal.ZERO) > 0)
                .bonusPointsUsed(priceDetails.bonusPointsUsed())
                .isOrderReturnable(isOrderReturnable)
                .build();
    }
}
