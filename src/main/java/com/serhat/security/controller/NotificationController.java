package com.serhat.security.controller;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.service.notification.NotificationService;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/post-notification")
    public ResponseEntity<NotificationAddedResponse> notification(@RequestParam NotificationTopic notificationTopic) {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(notificationService.addNotification(user, notificationTopic));
    }

    @GetMapping("/get-notifications")
    public ResponseEntity<List<NotificationDTO>> notifications() {
        return ResponseEntity.ok(notificationService.getNotifications());
    }
}