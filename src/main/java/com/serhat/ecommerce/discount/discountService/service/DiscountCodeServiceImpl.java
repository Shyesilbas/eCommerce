package com.serhat.ecommerce.discount.discountService.service;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.discount.discountService.mapper.DiscountMapper;
import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.dto.response.AvailableDiscountResponse;
import com.serhat.ecommerce.dto.response.DiscountDetails;
import com.serhat.ecommerce.dto.response.ExpiredDiscountResponse;
import com.serhat.ecommerce.dto.response.UsedDiscountResponse;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.enums.CouponStatus;
import com.serhat.ecommerce.discount.discountException.DiscountCodeNotFoundException;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class DiscountCodeServiceImpl implements DiscountCodeService {
    private final DiscountCodeProcessor discountCodeProcessor;
    private final DiscountValidationService discountValidationService;
    private final DiscountMapper discountMapper;
    private final UserService userService;

    @Override
    public Page<AvailableDiscountResponse> getAvailableDiscountCodes(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<DiscountCode> availableCodes = discountValidationService.findByUserAndStatus(user, CouponStatus.NOT_USED, pageable);
        if (availableCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No available discount codes found.");
        }
        return availableCodes.map(discountMapper::toAvailableDiscountResponse);
    }

    @Override
    public Page<UsedDiscountResponse> getUsedDiscountCodes(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<DiscountCode> usedCodes = discountValidationService.findByUserAndStatus(user, CouponStatus.USED, pageable);
        if (usedCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No used discount codes found.");
        }
        return usedCodes.map(discountMapper::toUsedDiscountResponse);
    }

    @Override
    public Page<ExpiredDiscountResponse> getExpiredDiscountCodes(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<DiscountCode> expiredCodes = discountValidationService.findByUserAndStatusAndExpiresAtBefore(
                user, CouponStatus.NOT_USED, LocalDateTime.now(), pageable);
        if (expiredCodes.isEmpty()) {
            throw new DiscountCodeNotFoundException("No expired discount codes found.");
        }
        return expiredCodes.map(discountMapper::toExpiredDiscountResponse);
    }

    @Override
    public DiscountCode generateDiscountCode() {
        User user = userService.getAuthenticatedUser();
        return discountCodeProcessor.generateDiscountCode(user);
    }

    @Override
    public DiscountDetails applyDiscount(OrderRequest orderRequest, BigDecimal originalPrice, User user) {
        if (orderRequest.discountId() == null) {
            return new DiscountDetails(BigDecimal.ZERO, null);
        }
        try {
            DiscountCode discountCode = discountValidationService.findById(orderRequest.discountId());
            discountValidationService.validateDiscountCode(discountCode, user);
            BigDecimal discountAmount = discountCodeProcessor.calculateDiscountAmount(originalPrice, discountCode);
            return new DiscountDetails(discountAmount, discountCode);
        } catch (DiscountCodeNotFoundException e) {
            log.warn("Discount code not found: {}", orderRequest.discountId());
            return new DiscountDetails(BigDecimal.ZERO, null);
        }
    }

    @Override
    public void handleDiscountCode(Order order, DiscountCode discountCode) {
        discountCodeProcessor.handleDiscountCode(order, discountCode);
    }

    @Override
    public BigDecimal getDiscountThreshold() {
        return discountCodeProcessor.getDiscountThreshold();
    }
}