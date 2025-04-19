package com.serhat.ecommerce.order.details;

import com.serhat.ecommerce.order.dto.response.OrderResponse;
import com.serhat.ecommerce.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailsService {
    Order findOrderById(Long orderId);
    OrderResponse getOrderDetails(Long orderId);
    Page<OrderResponse> getOrdersByUser(Pageable pageable);
    void updateOrderAfterCancellation(Order order);
}
