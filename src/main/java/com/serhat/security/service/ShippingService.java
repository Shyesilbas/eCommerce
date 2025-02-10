package com.serhat.security.service;

import com.serhat.security.entity.User;
import com.serhat.security.interfaces.ShippingInterface;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingService implements ShippingInterface {

    @Override
    public BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        switch (user.getMembershipPlan()) {
            case VIP -> {
                return BigDecimal.ZERO;
            }
            case PREMIUM -> {
                return (totalPrice.compareTo(BigDecimal.valueOf(100)) >= 0) ? BigDecimal.ZERO : BigDecimal.valueOf(6.99);
            }
            case BASIC -> {
                return (totalPrice.compareTo(BigDecimal.valueOf(200)) >= 0) ? BigDecimal.ZERO : BigDecimal.valueOf(10.99);
            }
        }
        return BigDecimal.ZERO;
    }
}
