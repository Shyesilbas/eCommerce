package com.serhat.ecommerce.notification.service;

import com.serhat.ecommerce.dto.object.NotificationDTO;
import com.serhat.ecommerce.dto.response.NotificationAddedResponse;
import com.serhat.ecommerce.notification.entity.Notification;
import com.serhat.ecommerce.notification.mapper.NotificationMapper;
import com.serhat.ecommerce.notification.repository.NotificationRepository;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.enums.NotificationTopic;
import com.serhat.ecommerce.notification.notificatioException.NoNotificationsFoundException;
import com.serhat.ecommerce.user.userS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    @Transactional
    @Override
    public NotificationAddedResponse addNotification(User user, NotificationTopic notificationTopic) {
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
    public List<NotificationDTO> getNotifications() {
        User user = getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUser(user);

        if (notifications.isEmpty()) {
            throw new NoNotificationsFoundException("No notifications found");
        }

        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }
}