package com.serhat.security.controller;

import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/post-notification")
    public ResponseEntity<NotificationAddedResponse> notification(HttpServletRequest request , NotificationTopic notificationTopic){
        return ResponseEntity.ok(notificationService.addNotification(request, notificationTopic));
    }
}
