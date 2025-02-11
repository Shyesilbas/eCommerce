package com.serhat.security.service;

import com.serhat.security.entity.User;
import com.serhat.security.interfaces.ShippingStrategy;
import com.serhat.security.service.shippingStrategy.ShippingStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ShippingService{

  private final ShippingStrategyFactory shippingStrategyFactory;

    public BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        ShippingStrategy strategy = shippingStrategyFactory.getShippingStrategy(user);
        return strategy.calculateShippingFee(user, totalPrice);
    }

}
