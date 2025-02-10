package com.serhat.security.interfaces;

import com.serhat.security.entity.User;

import java.math.BigDecimal;

public interface ShippingInterface {
    BigDecimal calculateShippingFee(User user, BigDecimal totalPrice);

}
