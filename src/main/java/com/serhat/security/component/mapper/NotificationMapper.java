package com.serhat.security.component.mapper;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.entity.Notification;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final UserRepository userRepository;
    public NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .at(notification.getAt())
                .notificationTopic(notification.getNotificationTopic())
                .message(generateNotificationMessage(notification))
                .userId(notification.getUser().getUserId())
                .build();
    }

    public Notification toEntity(NotificationDTO notificationDTO) {
        User user = userRepository.findById(notificationDTO.userId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + notificationDTO.userId()));

        return Notification.builder()
                .notificationId(notificationDTO.notificationId())
                .at(notificationDTO.at())
                .notificationTopic(notificationDTO.notificationTopic())
                .message(notificationDTO.message())
                .user(user)
                .build();
    }
    public String generateNotificationMessage(Notification notification) {
        return getNotificationDescription(notification.getNotificationTopic()) + " successfully!";
    }
    private String getNotificationDescription(NotificationTopic topic) {
        return switch (topic) {
            case ADDRESS_ADDED -> "New Address Added";
            case ADDRESS_DELETED -> "An Address Deleted";
            case ADDRESS_UPDATED -> "An Address Updated";
            case EMAIL_UPDATE -> "E-mail updated";
            case PASSWORD_UPDATE -> "Password Updated";
            case PHONE_UPDATE -> "Phone Number updated";
            case FAILED_LOGIN -> "Failed Login attempt!";
            default -> "Notification Received.";
        };
    }
}
