package com.serhat.security.interfaces;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;

public interface NotificationInterface {
    void addOrderNotification(User user, Order order, NotificationTopic topic);

}
