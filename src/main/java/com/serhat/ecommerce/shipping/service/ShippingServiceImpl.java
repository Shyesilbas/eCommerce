package com.serhat.ecommerce.shipping.service;

import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.shipping.shippingStrategy.ShippingStrategy;
import com.serhat.ecommerce.shipping.shippingStrategy.ShippingStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService{

  private final ShippingStrategyFactory shippingStrategyFactory;

    @Override
    public BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        ShippingStrategy strategy = shippingStrategyFactory.getShippingStrategy(user);
        return strategy.calculateShippingFee(user, totalPrice);
    }

}
