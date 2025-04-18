package com.serhat.ecommerce.discount.bonus.strategy;

import com.serhat.ecommerce.user.userS.entity.User;

import java.math.BigDecimal;

public interface BonusStrategy {
    BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice);

}
