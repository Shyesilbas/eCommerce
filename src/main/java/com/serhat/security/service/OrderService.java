package com.serhat.security.service;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderItemDetails;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.AddressRepository;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ShoppingCardRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCardRepository shoppingCardRepository;
    private final TokenInterface tokenInterface;
    private final AddressRepository addressRepository;


    public boolean isAddressBelongsToUser(Long addressId, Long userId) {
        return addressRepository.existsByAddressIdAndUserUserId(addressId, userId);
    }
    @Transactional
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = tokenInterface.getUserFromToken(request);

        if (!isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId())) {
            throw new RuntimeException("Shipping address does not belong to the user!");
        }

        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);
        if (shoppingCards.isEmpty()) {
            throw new RuntimeException("No products in the shopping card!");
        }

        BigDecimal totalPrice = shoppingCards.stream()
                .map(shoppingCard -> shoppingCard.getProduct().getPrice().multiply(new BigDecimal(shoppingCard.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status("PENDING")
                .shippingAddressId(orderRequest.shippingAddressId())
                .paymentMethod(orderRequest.paymentMethod())
                .notes(orderRequest.notes())
                .build();

        List<OrderItem> orderItems = shoppingCards.stream()
                .map(shoppingCard -> OrderItem.builder()
                        .order(order)
                        .product(shoppingCard.getProduct())
                        .quantity(shoppingCard.getQuantity())
                        .price(shoppingCard.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        orderRepository.save(order);

        shoppingCardRepository.deleteAll(shoppingCards);

        log.info("Order created for user: {}", user.getUsername());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .shippingAddressId(order.getShippingAddressId())
                .paymentMethod(order.getPaymentMethod())
                .notes(order.getNotes())
                .totalQuantity(orderItems.stream().mapToInt(OrderItem::getQuantity).sum())
                .orderItems(orderItems.stream()
                        .map(item -> OrderItemDetails.builder()
                                .productCode(item.getProduct().getProductCode())
                                .productName(item.getProduct().getName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public List<OrderResponse> getOrdersByUser(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        return orderRepository.findByUser(user).stream()
                .map(order -> OrderResponse.builder()
                        .orderId(order.getOrderId())
                        .totalPrice(order.getTotalPrice())
                        .orderDate(order.getOrderDate())
                        .status(order.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetails(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to view this order!");
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .shippingAddressId(order.getShippingAddressId())
                .paymentMethod(order.getPaymentMethod())
                .notes(order.getNotes())
                .totalQuantity(order.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum())
                .orderItems(order.getOrderItems().stream()
                        .map(item -> OrderItemDetails.builder()
                                .productCode(item.getProduct().getProductCode())
                                .productName(item.getProduct().getName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
