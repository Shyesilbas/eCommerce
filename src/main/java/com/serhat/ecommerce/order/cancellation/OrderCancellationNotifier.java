package com.serhat.ecommerce.order.cancellation;

import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.notification.service.NotificationService;
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
