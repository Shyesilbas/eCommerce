package com.serhat.ecommerce.shipping.shippingStrategy;

import com.serhat.ecommerce.user.userS.entity.User;

import java.math.BigDecimal;

public interface ShippingStrategy{
    BigDecimal calculateShippingFee(User user, BigDecimal totalPrice);

}
