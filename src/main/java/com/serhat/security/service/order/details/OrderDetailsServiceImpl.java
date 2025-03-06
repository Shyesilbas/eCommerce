package com.serhat.security.service.order.details;

import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.exception.NoOrderException;
import com.serhat.security.exception.OrderNotFoundException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.component.mapper.OrderMapper;
import com.serhat.security.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final TokenInterface tokenInterface;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException("order not found with id : "+orderId));
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = findOrderById(orderId);

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to view this order!");
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> getOrdersByUser(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<Order> orders = orderRepository.findByUser(user, pageable);

        if (orders.isEmpty()) {
            throw new NoOrderException("No orders found");
        }
        return orders.map(orderMapper::toOrderResponse);
    }
}
