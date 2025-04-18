package com.serhat.ecommerce.order.details;

import com.serhat.ecommerce.dto.response.OrderResponse;
import com.serhat.ecommerce.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailsService {
    Order findOrderById(Long orderId);
    OrderResponse getOrderDetails(Long orderId);
    Page<OrderResponse> getOrdersByUser(Pageable pageable);
    void updateOrderAfterCancellation(Order order);
}
