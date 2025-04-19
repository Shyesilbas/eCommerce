package com.serhat.ecommerce.notification.dto;

import com.serhat.ecommerce.notification.enums.NotificationTopic;

import java.time.LocalDateTime;

public record NotificationAddedResponse(
        String message,
        LocalDateTime at,
        NotificationTopic topic
) {
}
