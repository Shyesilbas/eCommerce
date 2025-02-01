package com.serhat.security.service;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderItemDetails;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.AddressRepository;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ShoppingCardRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCardRepository shoppingCardRepository;
    private final TokenInterface tokenInterface;
    private final AddressRepository addressRepository;

    private boolean isAddressBelongsToUser(Long addressId, Long userId) {
        return addressRepository.existsByAddressIdAndUserUserId(addressId, userId);
    }

    private AddressDto convertToAddressDto(Long addressId) {
        return addressRepository.findById(addressId)
                .map(address -> new AddressDto(
                        address.getAddressId(), address.getCountry(), address.getCity(),
                        address.getStreet(), address.getAptNo(), address.getFlatNo(),
                        address.getDescription(), address.getAddressType()))
                .orElseThrow(() -> new AddressNotFoundException("Address not found for ID: " + addressId));
    }

    private List<OrderItemDetails> convertToOrderItemDetails(List<OrderItem> orderItems) {
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

    private OrderResponse convertToOrderResponse(Order order) {
        AddressDto shippingAddress = convertToAddressDto(order.getShippingAddressId());
        List<OrderItemDetails> orderItems = convertToOrderItemDetails(order.getOrderItems());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .totalQuantity(order.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .notes(order.getNotes())
                .orderItems(orderItems)
                .shippingAddress(shippingAddress)
                .build();
    }

    @Transactional
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = tokenInterface.getUserFromToken(request);

        if (!isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId())) {
            throw new AddressNotBelongToUserException("Shipping address does not belong to the user!");
        }

        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);
        if (shoppingCards.isEmpty()) {
            throw new EmptyShoppingCardException("No products in the shopping cart!");
        }

        BigDecimal totalPrice = shoppingCards.stream()
                .map(sc -> sc.getProduct().getPrice().multiply(new BigDecimal(sc.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status(OrderStatus.APPROVED)
                .shippingAddressId(orderRequest.shippingAddressId())
                .paymentMethod(orderRequest.paymentMethod())
                .notes(orderRequest.notes())
                .updatedAt(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = shoppingCards.stream()
                .map(sc -> OrderItem.builder()
                        .order(order)
                        .product(sc.getProduct())
                        .quantity(sc.getQuantity())
                        .price(sc.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);
        shoppingCardRepository.deleteAll(shoppingCards);

        log.info("Order created for user: {}", user.getUsername());

        return convertToOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByUser(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<Order> orders = orderRepository.findByUser(user);

        if (orders.isEmpty()) {
            throw new NoOrderException("No orders found");
        }

        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            long minutesSinceOrder = java.time.Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes();

            if (order.getStatus() == OrderStatus.PENDING && minutesSinceOrder >= 15) {
                order.setStatus(OrderStatus.APPROVED);
                log.info("Order {} status updated to APPROVED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.APPROVED && minutesSinceOrder >= 60) {
                order.setStatus(OrderStatus.SHIPPED);
                log.info("Order {} status updated to SHIPPED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.SHIPPED && minutesSinceOrder >= 180) {
                order.setStatus(OrderStatus.DELIVERED);
                log.info("Order {} status updated to DELIVERED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.CANCELLED) {
                long minutesSinceCancellation = java.time.Duration.between(order.getUpdatedAt(), LocalDateTime.now()).toMinutes();
                if (minutesSinceCancellation >= 15) {
                    order.setStatus(OrderStatus.REFUNDED);
                    log.info("Order {} status updated to REFUNDED", order.getOrderId());
                }
            }
            orderRepository.saveAll(orders);
        }
    }

    public OrderCancellationResponse cancelOrder(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().equals(user)) {
            throw new WrongOrderIdException("Wrong Order Id!");
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException("Order cannot be canceled as it is already shipped or delivered!");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} has been canceled by user {}", order.getOrderId(), user.getUsername());

        return new OrderCancellationResponse(
                order.getTotalPrice(),
                convertToOrderItemDetails(order.getOrderItems()),
                order.getStatus(),
                LocalDateTime.now(),
                "Refund Fee will be deposited into your account as soon as possible."
        );
    }


    public OrderResponse getOrderDetails(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to view this order!");
        }

        return convertToOrderResponse(order);
    }
}
