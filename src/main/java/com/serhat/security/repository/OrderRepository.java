package com.serhat.security.repository;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus orderStatus);

    Optional<Order> findByDiscountCode(DiscountCode discountCode);

    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByStatusNot(OrderStatus orderStatus);

    List<Order> findByStatusIn(List<OrderStatus> approved);
}
