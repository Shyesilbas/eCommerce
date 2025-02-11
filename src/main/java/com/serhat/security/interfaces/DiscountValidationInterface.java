package com.serhat.security.interfaces;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface DiscountValidationInterface {
    void validateDiscountCode(DiscountCode discountCode, User user);
    DiscountCode findById(Long id);
}
