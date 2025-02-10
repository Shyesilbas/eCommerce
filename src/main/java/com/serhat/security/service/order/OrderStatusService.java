package com.serhat.security.service.order;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderStatusService {
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findByStatusIn(List.of(OrderStatus.APPROVED, OrderStatus.SHIPPED));
        for (Order order : orders) {
            long minutesSinceOrder = Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes();
            if (order.getStatus() == OrderStatus.APPROVED && minutesSinceOrder >= 60) {
                order.setStatus(OrderStatus.SHIPPED);
                notificationService.addOrderNotification(order.getUser(), order, NotificationTopic.ORDER_SHIPPED);
                log.info("Order {} status updated to SHIPPED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.SHIPPED && minutesSinceOrder >= 180) {
                order.setStatus(OrderStatus.DELIVERED);
                notificationService.addOrderNotification(order.getUser(), order, NotificationTopic.ORDER_DELIVERED);
                log.info("Order {} status updated to DELIVERED", order.getOrderId());
            }
        }
        orderRepository.saveAll(orders);
    }
}
