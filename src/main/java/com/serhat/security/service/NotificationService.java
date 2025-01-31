package com.serhat.security.service;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.Notification;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.exception.NoNotificationsFoundException;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.NotificationMapper;
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
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final TokenInterface tokenInterface;

    @Transactional
    public NotificationAddedResponse addNotification(HttpServletRequest request, NotificationTopic notificationTopic) {
        User user = tokenInterface.getUserFromToken(request);

        Notification notification = Notification.builder()
                .user(user)
                .at(LocalDateTime.now())
                .notificationTopic(notificationTopic)
                .build();

        String message = notificationMapper.generateNotificationMessage(notification);
        notification.setMessage(message);

        notificationRepository.save(notification);
        log.info("Notification added for user: {}, Topic: {}", user.getUsername(), notificationTopic);

        return new NotificationAddedResponse(
                message,
                notification.getAt(),
                notification.getNotificationTopic());
    }

    public List<NotificationDTO> getNotifications(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        List<Notification> notifications = notificationRepository.findByUser(user);

        if(notifications.isEmpty()){
            throw new NoNotificationsFoundException("No notification");
        }

        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }
}