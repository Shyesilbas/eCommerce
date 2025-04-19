package com.serhat.ecommerce.notification.dto;

import com.serhat.ecommerce.notification.enums.NotificationTopic;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationDTO(
         Long notificationId,
         LocalDateTime at,
         NotificationTopic notificationTopic,
         String message,
         Long userId
) {
}
