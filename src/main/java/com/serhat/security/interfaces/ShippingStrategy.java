package com.serhat.security.interfaces;

import com.serhat.security.entity.User;

import java.math.BigDecimal;

public interface ShippingStrategy{
    BigDecimal calculateShippingFee(User user, BigDecimal totalPrice);

}
