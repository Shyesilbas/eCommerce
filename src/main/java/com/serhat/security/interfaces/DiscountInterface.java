package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface DiscountInterface {
    DiscountDetails applyDiscount(OrderRequest orderRequest, BigDecimal originalPrice, User user);
    void validateDiscountCode(DiscountCode discountCode, User user);
    void handleDiscountCode(Order order, DiscountCode discountCode, HttpServletRequest request);
    void markExpiredDiscountCodes();
}
