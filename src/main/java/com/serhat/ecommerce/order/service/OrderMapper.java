package com.serhat.ecommerce.order.service;

import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.order.entity.OrderItem;
import com.serhat.ecommerce.user.address.mapper.AddressMapper;
import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.user.address.dto.object.AddressDto;
import com.serhat.ecommerce.order.dto.request.OrderRequest;
import com.serhat.ecommerce.order.dto.response.OrderCancellationResponse;
import com.serhat.ecommerce.order.dto.response.OrderItemDetails;
import com.serhat.ecommerce.order.dto.response.OrderResponse;
import com.serhat.ecommerce.product.dto.PriceDetails;
import com.serhat.ecommerce.discount.enums.DiscountRate;
import com.serhat.ecommerce.order.enums.OrderStatus;
import com.serhat.ecommerce.discount.discountService.service.DiscountCodeService;
import com.serhat.ecommerce.discount.giftCard.service.GiftCardService;
import com.serhat.ecommerce.user.userS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final AddressMapper addressMapper;
    private final DiscountCodeService discountService;
    private final GiftCardService giftCardService;

    public OrderCancellationResponse toOrderCancellationResponse(Order order, BigDecimal totalPaid) {
        BigDecimal giftCardAmount = (order.getGiftCard() != null) ? order.getGiftCard().getGiftAmount().getAmount() : BigDecimal.ZERO;

        return new OrderCancellationResponse(
                order.getTotalPrice(),
                order.getShippingFee(),
                order.getIsBonusPointUsed(),
                order.getBonusPointsUsed(),
                order.getTotalDiscount(),
                giftCardAmount,
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
        String discountMessage = (order.getTotalPaid().compareTo(discountService.getDiscountThreshold()) >= 0)
                ? "You’ve been granted a discount code!"
                : "Add more to obtain a discount code.";

        BigDecimal giftCardAmount = (order.getGiftCard() != null) ? order.getGiftCard().getGiftAmount().getAmount() : BigDecimal.ZERO;

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
                giftCardAmount,
                order.getTotalPaid(),
                order.getTotalSaved(),
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

        Optional<GiftCard> giftCardOptional = orderRequest.giftCardId() != null ?
                Optional.ofNullable(giftCardService.findById(orderRequest.giftCardId())) :
                Optional.empty();

        return Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(priceDetails.totalPrice())
                .status(OrderStatus.APPROVED)
                .shippingAddressId(orderRequest.shippingAddressId())
                .paymentMethod(orderRequest.paymentMethod())
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
                .giftCard(giftCardOptional.orElse(null))
                .totalSaved(priceDetails.totalSaved())
                .build();
    }

    public List<OrderItem> convertShoppingCartToOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        return shoppingCards.stream()
                .map(sc -> OrderItem.builder()
                        .order(order)
                        .product(sc.getProduct())
                        .quantity(sc.getQuantity())
                        .price(sc.getProduct().getPrice())
                        .isReturnable(sc.getProduct().isReturnable())
                        .build())
                .collect(Collectors.toList());
    }
}