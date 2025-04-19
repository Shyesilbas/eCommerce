package com.serhat.ecommerce.discount.discountService.repository;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.enums.CouponStatus;
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
