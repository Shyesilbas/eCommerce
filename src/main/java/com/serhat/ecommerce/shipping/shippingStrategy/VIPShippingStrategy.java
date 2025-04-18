package com.serhat.ecommerce.shipping.shippingStrategy;

import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VIPShippingStrategy implements ShippingStrategy {

    @Override
    public BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        return BigDecimal.ZERO;
    }
}
