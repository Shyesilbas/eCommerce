package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.NotificationTopic;

import java.time.LocalDateTime;

public record NotificationAddedResponse(
        String message,
        LocalDateTime at,
        NotificationTopic topic
) {
}
