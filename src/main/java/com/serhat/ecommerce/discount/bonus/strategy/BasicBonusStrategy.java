package com.serhat.ecommerce.discount.bonus.strategy;

import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("BASIC")
public class BasicBonusStrategy implements BonusStrategy {
    @Override
    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        return totalPrice.multiply(BigDecimal.valueOf(0.01));

    }
}
