package com.serhat.security.service.order.cancellation;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.service.order.OrderStockService;
import com.serhat.security.service.order.details.OrderDetailsService;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
class OrderCancellationProcessor {
    private final UserService userService;
    private final OrderDetailsService orderDetailsService;
    private final OrderCancellationValidationService orderCancellationValidationService;
    private final OrderRefundService orderRefundService;
    private final OrderStockService orderStockService;
    private final OrderCancellationNotifier orderCancellationNotifier;

    public Order processCancellation(Long orderId) {
        User user = userService.getAuthenticatedUser();
        Order order = orderDetailsService.findOrderById(orderId);
        orderCancellationValidationService.checkIsOrderCancellable(order, user);
        orderRefundService.refundOrder(order, user);
        orderStockService.updateStockAfterCancellation(order);
        orderCancellationNotifier.notifyUser(order, user);
        orderDetailsService.updateOrderAfterCancellation(order);
        return order;
    }

}
