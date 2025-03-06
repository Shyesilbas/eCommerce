package com.serhat.security.service.order.cancellation;

import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;


public interface OrderCancellationService {
    OrderCancellationResponse cancelOrder(Long orderId);
    void finalizeCancellation(Order order, User user);
    void updateProductsAfterCancellation(Order order);
    void updateOrderAfterCancellation(Order order);
}