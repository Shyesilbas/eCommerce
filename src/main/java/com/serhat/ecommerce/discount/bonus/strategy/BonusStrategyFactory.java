package com.serhat.ecommerce.discount.bonus.strategy;

import com.serhat.ecommerce.user.userS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BonusStrategyFactory {
    private final Map<String, BonusStrategy> strategyMap;

    public BonusStrategy getBonusStrategy(User user) {
        return strategyMap.getOrDefault(user.getMembershipPlan().name(), strategyMap.get("BASIC"));
    }

}
