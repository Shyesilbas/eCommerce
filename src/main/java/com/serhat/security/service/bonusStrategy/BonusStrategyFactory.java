package com.serhat.security.service.bonusStrategy;

import com.serhat.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BonusStrategyFactory {
    private final VIPBonusStrategy vipBonusStrategy;
    private final PremiumBonusStrategy premiumBonusStrategy;
    private final BasicBonusStrategy basicBonusStrategy;

    public BonusStrategy getBonusStrategy(User user) {
        return switch (user.getMembershipPlan()) {
            case VIP -> vipBonusStrategy;
            case PREMIUM -> premiumBonusStrategy;
            case BASIC -> basicBonusStrategy;
        };
    }

}
