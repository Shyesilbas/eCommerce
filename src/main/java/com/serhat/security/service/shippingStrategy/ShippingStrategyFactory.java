package com.serhat.security.service.shippingStrategy;

import com.serhat.security.entity.User;
import com.serhat.security.interfaces.ShippingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingStrategyFactory {

    private final VIPShippingStrategy vipShippingStrategy;
    private final PremiumShippingStrategy premiumShippingStrategy;
    private final BasicShippingStrategy basicShippingStrategy;
    public ShippingStrategy getShippingStrategy(User user) {
        return switch (user.getMembershipPlan()) {
            case VIP -> vipShippingStrategy;
            case PREMIUM -> premiumShippingStrategy;
            case BASIC -> basicShippingStrategy;
        };
    }
}
