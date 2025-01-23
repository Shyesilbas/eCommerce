package com.serhat.security.service;

import com.serhat.security.dto.object.NotificationDTO;
import com.serhat.security.dto.response.NotificationAddedResponse;
import com.serhat.security.entity.Notification;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.mapper.NotificationMapper;
import com.serhat.security.repository.NotificationRepository;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final NotificationMapper notificationMapper;

    @Transactional
    public NotificationAddedResponse addNotification(HttpServletRequest servletRequest, NotificationTopic notificationTopic) {
        String token = extractTokenFromRequest(servletRequest);
        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            throw new RuntimeException("Username not found in token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        Notification notification = Notification.builder()
                .user(user)
                .at(LocalDateTime.now())
                .notificationTopic(notificationTopic)
                .build();

        String message = notificationMapper.generateNotificationMessage(notification);
        notification.setMessage(message);

        notificationRepository.save(notification);
        log.info("Notification added for user: {}, Topic: {}", username, notificationTopic);

        return new NotificationAddedResponse(
                message,
                notification.getAt(),
                notification.getNotificationTopic());
    }


    public List<NotificationDTO> getNotifications(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            throw new RuntimeException("Username not found in token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        return notificationRepository.findByUser(user).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return token;
    }
}