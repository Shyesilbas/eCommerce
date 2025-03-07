package com.serhat.security.service.order.details;

import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.exception.NoOrderException;
import com.serhat.security.exception.OrderNotFoundException;
import com.serhat.security.component.mapper.OrderMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final UserService userService;

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException("order not found with id : "+orderId));
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        User user = userService.getAuthenticatedUser();
        Order order = findOrderById(orderId);

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to view this order!");
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> getOrdersByUser( Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<Order> orders = orderRepository.findByUser(user, pageable);

        if (orders.isEmpty()) {
            throw new NoOrderException("No orders found");
        }
        return orders.map(orderMapper::toOrderResponse);
    }


    public void updateOrderAfterCancellation(Order order) {
        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
