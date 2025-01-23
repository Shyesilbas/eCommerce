package com.serhat.security.controller;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/post-notification")
    public ResponseEntity<NotificationAddedResponse> notification(HttpServletRequest request , NotificationTopic notificationTopic){
        return ResponseEntity.ok(notificationService.addNotification(request, notificationTopic));
    }

    @GetMapping("get-notifications")
    public ResponseEntity<List<NotificationDTO>> notifications(HttpServletRequest request){
        return ResponseEntity.ok(notificationService.getNotifications(request));
    }


}
