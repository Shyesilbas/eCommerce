package com.serhat.security.service.bonus;

import com.serhat.security.dto.response.BonusPointInformation;
import com.serhat.security.entity.User;
import com.serhat.security.service.bonus.strategy.BonusStrategy;
import com.serhat.security.service.bonus.strategy.BonusStrategyFactory;
import com.serhat.security.service.user.UserService;
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