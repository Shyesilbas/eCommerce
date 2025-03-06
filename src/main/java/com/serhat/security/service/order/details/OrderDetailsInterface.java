package com.serhat.security.service.order.details;

import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailsInterface {
    Order findOrderById(Long orderId);

    OrderResponse getOrderDetails(Long orderId, HttpServletRequest request);
    Page<OrderResponse> getOrdersByUser(HttpServletRequest request, Pageable pageable);
}
