package com.serhat.security.service.discountService;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface DiscountValidationService {
    DiscountCode findById(Long id);
    void validateDiscountCode(DiscountCode discountCode, User user);
    void saveDiscountCode(DiscountCode discountCode);
    Page<DiscountCode> findByUserAndStatus(User user, CouponStatus status, Pageable pageable);
    Page<DiscountCode> findByUserAndStatusAndExpiresAtBefore(User user, CouponStatus status, LocalDateTime date, Pageable pageable);
}