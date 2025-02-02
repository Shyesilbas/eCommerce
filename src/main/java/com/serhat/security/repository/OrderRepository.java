package com.serhat.security.repository;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus orderStatus);
}
