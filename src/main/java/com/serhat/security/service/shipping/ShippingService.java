package com.serhat.security.service.shipping;

import com.serhat.security.entity.User;

import java.math.BigDecimal;

public interface ShippingService {
    BigDecimal calculateShippingFee(User user, BigDecimal totalPrice);
}
