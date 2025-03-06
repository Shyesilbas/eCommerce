package com.serhat.security.service.notification;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.Notification;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.exception.NoNotificationsFoundException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.component.mapper.NotificationMapper;
import com.serhat.security.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final TokenInterface tokenInterface;

    @Transactional
    public NotificationAddedResponse addNotification(HttpServletRequest request, NotificationTopic notificationTopic) {
        User user = tokenInterface.getUserFromToken(request);

        Notification notification = createNotification(user, notificationTopic);

        notificationRepository.save(notification);
        log.info("Notification added for user: {}, Topic: {}", user.getUsername(), notificationTopic);

        return new NotificationAddedResponse(
                notification.getMessage(),
                notification.getAt(),
                notification.getNotificationTopic());
    }
    private Notification createNotification(User user, NotificationTopic notificationTopic) {
        Notification notification = Notification.builder()
                .user(user)
                .at(LocalDateTime.now())
                .notificationTopic(notificationTopic)
                .build();

        String message = notificationMapper.generateNotificationMessage(notification);
        notification.setMessage(message);
        return notification;
    }

    @Transactional
    @Override
    public void addOrderCreationNotification(User user, Order order) {
        addOrderStatusNotification(user, order, NotificationTopic.ORDER_PLACED);
    }

    @Transactional
    @Override
    public void addOrderCancellationNotification(User user, Order order) {
        addOrderStatusNotification(user, order, NotificationTopic.ORDER_CANCELLED);
    }

    @Transactional
    @Override
    public void addOrderShippedNotification(User user, Order order) {
        addOrderStatusNotification(user, order, NotificationTopic.ORDER_SHIPPED);
    }

    @Transactional
    @Override
    public void addOrderDeliveredNotification(User user, Order order) {
        addOrderStatusNotification(user, order, NotificationTopic.ORDER_DELIVERED);
    }

    private void addOrderStatusNotification(User user, Order order, NotificationTopic topic) {
        Notification notification = createNotification(user, topic);
        notification.setMessage("Your order #" + order.getOrderId() + " is now " + topic.name().replace("_", " ").toLowerCase());

        notificationRepository.save(notification);
        log.info("Notification added: User {}, Order ID {}, Topic {}", user.getUsername(), order.getOrderId(), topic);
    }

    @Override
    public List<NotificationDTO> getNotifications(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        List<Notification> notifications = notificationRepository.findByUser(user);

        if (notifications.isEmpty()) {
            throw new NoNotificationsFoundException("No notifications found");
        }

        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }
}
