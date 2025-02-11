package com.serhat.security.interfaces;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface NotificationInterface {
    void addOrderCreationNotification(User user, Order order);
    List<NotificationDTO> getNotifications(HttpServletRequest request);
    void addOrderCancellationNotification(User user, Order order);
    void addOrderShippedNotification(User user, Order order);
    void addOrderDeliveredNotification(User user, Order order);

}
