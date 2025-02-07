package com.serhat.security.repository;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode,Long> {
    Page<DiscountCode> findByUserAndStatusAndExpiresAtBefore(User user, CouponStatus status, LocalDateTime now , Pageable pageable);

    Page<DiscountCode> findByStatusAndExpiresAtBefore(CouponStatus status, LocalDateTime now , Pageable pageable);

    Page<DiscountCode> findByUserAndStatus(User user, CouponStatus couponStatus, Pageable pageable);
}
