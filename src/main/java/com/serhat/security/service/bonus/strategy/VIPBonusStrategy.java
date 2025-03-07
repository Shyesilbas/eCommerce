package com.serhat.security.service.bonus.strategy;

import com.serhat.security.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("VIP")
public class VIPBonusStrategy implements BonusStrategy {
    @Override
    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        return totalPrice.multiply(BigDecimal.valueOf(0.05));
    }
}
