package com.serhat.security.service.order;

import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.interfaces.*;
import com.serhat.security.mapper.OrderMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.service.payment.PaymentProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderCancellationService implements OrderCancellationInterface {
    private final OrderRepository orderRepository;
    private final OrderDetailsInterface orderDetailsInterface;
    private final TokenInterface tokenInterface;
    private final ProductRepository productRepository;
    private final NotificationInterface notificationInterface;
    private final OrderMapper orderMapper;
    private final OrderCancellationValidationService orderCancellationValidationService;
    private final PaymentProcessingService paymentProcessingService;


    public Order findOrderById(Long orderId) {
     return orderDetailsInterface.findOrderById(orderId);
    }

    public void checkIsOrderCancellable(Order order , User user){
        orderCancellationValidationService.checkIsOrderCancellable(order, user);
    }

    @Transactional
    @Override
    @CacheEvict(value = "userInfoCache", key = "#request.userPrincipal.name")
    public OrderCancellationResponse cancelOrder(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = findOrderById(orderId);
        finalizeCancellation(order, user);
        return orderMapper.toOrderCancellationResponse(order, order.getTotalPaid());
    }

    @Override
    public void finalizeCancellation(Order order, User user) {
        BigDecimal shippingFee = order.getShippingFee();
        BigDecimal totalPaid = order.getTotalPaid();
        checkIsOrderCancellable(order,user);
        processRefundPayment(order,order.getPaymentMethod());
        updateUserAfterOrderCancel(user, order, shippingFee, totalPaid);
        updateOrderAfterCancellation(order);
        updateProductsAfterCancellation(order);
        addOrderCancellationNotification(user,order);
    }

    public void addOrderCancellationNotification(User user , Order order){
        notificationInterface.addOrderCancellationNotification(user, order);
    }

    @Override
    public void processRefundPayment(Order order, PaymentMethod paymentMethod) {
        paymentProcessingService.processPayment(order, paymentMethod);
    }

    private void updateProductsAfterCancellation(Order order){
        productRepository.saveAll(order.getOrderItems().stream().map(OrderItem::getProduct).toList());
        updateProductStockAfterCancellation(order);
    }

    @Override
    public void updateOrderAfterCancellation(Order order){
        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
