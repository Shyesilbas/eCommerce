package com.serhat.security.interfaces;

import com.serhat.security.dto.response.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailsInterface {
    OrderResponse getOrderDetails(Long orderId, HttpServletRequest request);
    Page<OrderResponse> getOrdersByUser(HttpServletRequest request, Pageable pageable);
}
