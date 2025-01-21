package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.NotificationTopic;

import java.time.LocalDateTime;

public record NotificationAddedResponse(
        String message,
        LocalDateTime at,
        NotificationTopic topic
) {
}
