package com.serhat.security.service.order.cancellation;

import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.component.mapper.OrderMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.service.inventory.InventoryService;
import com.serhat.security.service.notification.NotificationService;
import com.serhat.security.service.order.details.OrderDetailsService;
import com.serhat.security.service.payment.TransactionService;
import com.serhat.security.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationServiceImpl implements OrderCancellationService {
    private final OrderRepository orderRepository;
    private final OrderDetailsService orderDetailsService;
    private final TokenInterface tokenInterface;
    private final NotificationService notificationService;
    private final OrderMapper orderMapper;
    private final OrderCancellationValidationService orderCancellationValidationService;
    private final InventoryService inventoryService;
    private final UserService userService;
    private final TransactionService transactionService;

    @Transactional
    @Override
    @CacheEvict(value = "userInfoCache", key = "#request.userPrincipal.name")
    public OrderCancellationResponse cancelOrder(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = orderDetailsService.findOrderById(orderId);
        log.info("Total paid : "+order.getTotalPaid());
        finalizeCancellation(order, user);
        return orderMapper.toOrderCancellationResponse(order, order.getTotalPaid());
    }

    @Override
    public void finalizeCancellation(Order order, User user) {
        orderCancellationValidationService.checkIsOrderCancellable(order, user);
        Wallet wallet =user.getWallet(); // *
        log.info("Wallet balance before refund: {}", wallet.getBalance()); // *
        transactionService.createRefundTransaction(order,user,order.getTotalPaid(),order.getShippingFee()); // * refund will be transferred to e-wallet
        userService.updateUserAfterOrderCancel(user, order);
        updateOrderAfterCancellation(order);
        updateProductsAfterCancellation(order);
        notificationService.addOrderCancellationNotification(user, order);
    }

    @Override
    public void updateProductsAfterCancellation(Order order) {
        order.getOrderItems().forEach(orderItem ->
                inventoryService.updateProductStockAfterOrderCancellation(orderItem.getProduct(), orderItem.getQuantity()));
    }

    @Override
    public void updateOrderAfterCancellation(Order order) {
        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}