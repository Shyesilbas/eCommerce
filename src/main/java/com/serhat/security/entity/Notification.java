package com.serhat.security.entity;

import com.serhat.security.entity.enums.NotificationTopic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String username;
    private LocalDateTime at;
    @Enumerated(EnumType.STRING)
    private NotificationTopic notificationTopic;

}
