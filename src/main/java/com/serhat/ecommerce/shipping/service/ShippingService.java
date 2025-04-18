package com.serhat.ecommerce.shipping.service;

import com.serhat.ecommerce.user.userS.entity.User;

import java.math.BigDecimal;

public interface ShippingService {
    BigDecimal calculateShippingFee(User user, BigDecimal totalPrice);
}
