package com.serhat.ecommerce.order.repo;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByDiscountCode(DiscountCode discountCode);

    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByStatusNot(OrderStatus orderStatus);

    List<Order> findByStatusInAndOrderDateBefore(List<OrderStatus> approved, LocalDateTime localDateTime);
}
