package com.serhat.security.service.bonusStrategy;

import com.serhat.security.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PremiumBonusStrategy implements BonusStrategy {
    @Override
    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        return totalPrice.multiply(BigDecimal.valueOf(0.03));
    }
}
