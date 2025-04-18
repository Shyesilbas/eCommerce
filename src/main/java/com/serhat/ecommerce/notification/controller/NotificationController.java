package com.serhat.ecommerce.notification.controller;

import com.serhat.ecommerce.dto.object.NotificationDTO;
import com.serhat.ecommerce.dto.response.NotificationAddedResponse;
import com.serhat.ecommerce.notification.service.NotificationService;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.enums.NotificationTopic;
import com.serhat.ecommerce.user.userS.service.UserService;
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