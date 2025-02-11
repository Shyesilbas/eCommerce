package com.serhat.security.service.order;

import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.interfaces.NotificationInterface;
import com.serhat.security.interfaces.OrderCancellationInterface;
import com.serhat.security.interfaces.OrderCreationInterface;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.OrderMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final OrderCreationInterface orderInterface;
    private final TokenInterface tokenInterface;
    private final ProductRepository productRepository;
    private final TransactionService transactionService;
    private final NotificationInterface notificationInterface;
    private final OrderMapper orderMapper;
    private final OrderCancellationValidationService orderCancellationValidationService;

    @Override
    public void updateProductStockAfterCancellation(Order order) {
        OrderCancellationInterface.super.updateProductStockAfterCancellation(order);
    }
    public Order findOrderById(Long orderId) {
     return orderInterface.findOrderById(orderId);
    }
    public void checkIsOrderCancellable(Order order , User user){
        orderCancellationValidationService.checkIsOrderCancellable(order, user);
    }

    @Transactional
    @Override
    public OrderCancellationResponse cancelOrder(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = findOrderById(orderId);
        checkIsOrderCancellable(order,user);
        finalizeCancellation(order, user);
        return orderMapper.toOrderCancellationResponse(order, order.getTotalPaid());
    }

    @Override
    public void finalizeCancellation(Order order, User user) {
        BigDecimal shippingFee = order.getShippingFee();
        BigDecimal totalPaid = order.getTotalPaid();
        if (order.getPaymentMethod().equals(PaymentMethod.E_WALLET)) {
            transactionService.createRefundTransaction(order, user, totalPaid, shippingFee);
        }
        updateProductStockAfterCancellation(order);
        updateUserAfterOrderCancel(user, order, shippingFee, totalPaid);

        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        productRepository.saveAll(order.getOrderItems().stream().map(OrderItem::getProduct).toList());
        addOrderCancellationNotification(user,order);
    }

    public void addOrderCancellationNotification(User user , Order order){
        notificationInterface.addOrderCancellationNotification(user, order);
    }
}
