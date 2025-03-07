package com.serhat.security.service.discountService;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.exception.CouponAlreadyUsedException;
import com.serhat.security.exception.DiscountCodeExpiredException;
import com.serhat.security.exception.DiscountCodeNotFoundException;
import com.serhat.security.exception.InvalidDiscountCodeException;
import com.serhat.security.repository.DiscountCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiscountValidationServiceImpl implements DiscountValidationService {
    private final DiscountCodeRepository discountCodeRepository;

    @Override
    public DiscountCode findById(Long id) {
        return discountCodeRepository.findById(id)
                .orElseThrow(() -> new DiscountCodeNotFoundException("Discount code not found with ID: " + id));
    }

    @Override
    public void validateDiscountCode(DiscountCode discountCode, User user) {
        if (discountCode.getUser() != null && !discountCode.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidDiscountCodeException("This discount code is not valid for the current user!");
        }
        if (discountCode.getExpiresAt() != null && discountCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new DiscountCodeExpiredException("This discount code has expired!");
        }
        if (discountCode.getStatus().equals(CouponStatus.USED)) {
            throw new CouponAlreadyUsedException("The discount code you entered is already used!");
        }
    }

    @Override
    public void saveDiscountCode(DiscountCode discountCode) {
        discountCodeRepository.save(discountCode);
    }

    @Override
    public Page<DiscountCode> findByUserAndStatus(User user, CouponStatus status, Pageable pageable) {
        return discountCodeRepository.findByUserAndStatus(user, status, pageable);
    }

    @Override
    public Page<DiscountCode> findByUserAndStatusAndExpiresAtBefore(User user, CouponStatus status, LocalDateTime date, Pageable pageable) {
        return discountCodeRepository.findByUserAndStatusAndExpiresAtBefore(user, status, date, pageable);
    }
}