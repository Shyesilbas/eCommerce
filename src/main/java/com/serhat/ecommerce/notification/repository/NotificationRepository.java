package com.serhat.ecommerce.notification.repository;

import com.serhat.ecommerce.notification.entity.Notification;
import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUser(User user);
}
