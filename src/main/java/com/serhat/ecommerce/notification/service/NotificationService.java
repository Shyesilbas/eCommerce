package com.serhat.ecommerce.notification.service;

import com.serhat.ecommerce.dto.object.NotificationDTO;
import com.serhat.ecommerce.dto.response.NotificationAddedResponse;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.enums.NotificationTopic;

import java.util.List;

public interface NotificationService {
    NotificationAddedResponse addNotification(User user, NotificationTopic notificationTopic);
    void addOrderCreationNotification(User user, Order order);
    void addOrderCancellationNotification(User user, Order order);
    void addOrderShippedNotification(User user, Order order);
    void addOrderDeliveredNotification(User user, Order order);
    List<NotificationDTO> getNotifications();
}