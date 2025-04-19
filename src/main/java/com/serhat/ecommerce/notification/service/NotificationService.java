package com.serhat.ecommerce.notification.service;

import com.serhat.ecommerce.notification.dto.NotificationDTO;
import com.serhat.ecommerce.notification.dto.NotificationAddedResponse;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.notification.enums.NotificationTopic;

import java.util.List;

public interface NotificationService {
    NotificationAddedResponse addNotification(User user, NotificationTopic notificationTopic);
    void addOrderCreationNotification(User user, Order order);
    void addOrderCancellationNotification(User user, Order order);
    void addOrderShippedNotification(User user, Order order);
    void addOrderDeliveredNotification(User user, Order order);
    List<NotificationDTO> getNotifications();
}