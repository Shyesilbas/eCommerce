package com.serhat.ecommerce.discount.bonus.service;

import com.serhat.ecommerce.discount.bonus.strategy.BonusStrategy;
import com.serhat.ecommerce.dto.response.BonusPointInformation;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.bonus.strategy.BonusStrategyFactory;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BonusDetailsService {
    private final UserService userService;
    private final BonusStrategyFactory bonusStrategyFactory;

    public BonusPointInformation bonusPointInformation() {
        User user = userService.getAuthenticatedUser();
        return new BonusPointInformation(
                user.getBonusPointsWon(),
                user.getCurrentBonusPoints()
        );
    }

    public BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        BonusStrategy strategy = bonusStrategyFactory.getBonusStrategy(user);
        return strategy.calculateBonusPoints(user, totalPrice);
    }
}