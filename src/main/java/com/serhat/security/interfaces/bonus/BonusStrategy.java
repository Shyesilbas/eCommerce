package com.serhat.security.interfaces.bonus;

import com.serhat.security.entity.User;

import java.math.BigDecimal;

public interface BonusStrategy {
    BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice);

}
