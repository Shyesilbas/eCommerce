package com.serhat.security.repository;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode,Long> {
    List<DiscountCode> findByUserAndStatusAndExpiresAtBefore(User user, CouponStatus status, LocalDateTime now);

    List<DiscountCode> findByStatusAndExpiresAtBefore(CouponStatus status, LocalDateTime now);

    List<DiscountCode> findByUserAndStatus(User user, CouponStatus couponStatus);
}
