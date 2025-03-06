package com.serhat.security.service.order.details;

import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailsService {
    Order findOrderById(Long orderId);
    OrderResponse getOrderDetails(Long orderId);
    Page<OrderResponse> getOrdersByUser(Pageable pageable);
}
