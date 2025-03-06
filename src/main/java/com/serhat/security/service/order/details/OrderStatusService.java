package com.serhat.security.service.order.details;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.service.notification.NotificationService;
import com.serhat.security.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusService {
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Transactional
    @PostConstruct
    @Scheduled(fixedRate = 60000)
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findByStatusInAndOrderDateBefore(
                List.of(OrderStatus.APPROVED, OrderStatus.SHIPPED),
                LocalDateTime.now().minusMinutes(60));

        for (Order order : orders) {
            long minutesSinceOrder = Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes();

            if (order.getStatus() == OrderStatus.APPROVED && minutesSinceOrder >= 60) {
                order.setStatus(OrderStatus.SHIPPED);
                addOrderShippedNotification(order, order.getUser());
                log.info("Order {} status updated to SHIPPED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.SHIPPED && minutesSinceOrder >= 180) {
                order.setStatus(OrderStatus.DELIVERED);
                addOrderDeliveredNotification(order, order.getUser());
                log.info("Order {} status updated to DELIVERED", order.getOrderId());
            }
        }
        if (!orders.isEmpty()) {
            orderRepository.saveAll(orders);
        }
    }

    public void addOrderShippedNotification(Order order , User user){
        notificationService.addOrderShippedNotification(user, order);
    }
    public void addOrderDeliveredNotification(Order order , User user){
        notificationService.addOrderDeliveredNotification(user, order);
    }


}
