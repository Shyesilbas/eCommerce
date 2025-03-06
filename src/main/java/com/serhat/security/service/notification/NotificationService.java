package com.serhat.security.service.notification;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface NotificationService {
    void addOrderCreationNotification(User user, Order order);
    List<NotificationDTO> getNotifications(HttpServletRequest request);
    void addOrderCancellationNotification(User user, Order order);
    void addOrderShippedNotification(User user, Order order);
    void addOrderDeliveredNotification(User user, Order order);
    NotificationAddedResponse addNotification(HttpServletRequest request, NotificationTopic notificationTopic);

}
