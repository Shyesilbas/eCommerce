package com.serhat.security.service.shipping;

import com.serhat.security.entity.User;
import com.serhat.security.service.shippingStrategy.ShippingStrategy;
import com.serhat.security.service.shippingStrategy.ShippingStrategyFactory;
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
