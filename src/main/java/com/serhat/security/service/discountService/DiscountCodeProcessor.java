package com.serhat.security.service.discountService;

import com.serhat.security.component.mapper.DiscountMapper;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.DiscountRate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class DiscountCodeProcessor {
    private final DiscountValidationService discountValidationService;
    private final DiscountMapper discountMapper;

    @Value("${discount.code.threshold}")
    private BigDecimal discountThreshold;

    public DiscountCode generateDiscountCode(User user) {
        DiscountRate discountRate = determineDiscountRate();
        DiscountCode discountCode = discountMapper.createDiscountCode(user, discountRate);
        discountValidationService.saveDiscountCode(discountCode);
        return discountCode;
    }

    public BigDecimal calculateDiscountAmount(BigDecimal originalPrice, DiscountCode discountCode) {
        return originalPrice.multiply(BigDecimal.valueOf(discountCode.getDiscountRate().getPercentage() / 100.0));
    }

    public void updateCouponStatusToUsed(DiscountCode discountCode) {
        if (discountCode != null) {
            discountCode.setStatus(CouponStatus.USED);
            discountValidationService.saveDiscountCode(discountCode);
        }
    }

    public void handleDiscountCode(Order order, DiscountCode discountCode) {
        if (order != null && order.getTotalPrice().compareTo(discountThreshold) >= 0) {
            generateDiscountCode(order.getUser());
        }
        updateCouponStatusToUsed(discountCode);
    }

    private DiscountRate determineDiscountRate() {
        double random = Math.random();
        if (random < 0.5) return DiscountRate.TEN_PERCENT;
        else if (random < 0.8) return DiscountRate.TWENTY_PERCENT;
        else return DiscountRate.THIRTY_PERCENT;
    }


}