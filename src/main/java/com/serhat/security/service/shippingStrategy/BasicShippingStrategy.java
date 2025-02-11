package com.serhat.security.service.shippingStrategy;

import com.serhat.security.entity.User;
import com.serhat.security.interfaces.ShippingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BasicShippingStrategy implements ShippingStrategy {
    @Override
    public BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        return totalPrice.compareTo(BigDecimal.valueOf(200)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(10.99);
    }
}
