package com.serhat.security.service.order.cancellation;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class OrderCancellationNotifier {
    private final NotificationService notificationService;

    public void notifyUser(Order order, User user) {
        notificationService.addOrderCancellationNotification(user, order);
    }
}
