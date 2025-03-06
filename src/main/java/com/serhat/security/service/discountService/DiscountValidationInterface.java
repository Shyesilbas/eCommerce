package com.serhat.security.service.discountService;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;

public interface DiscountValidationInterface {
    void validateDiscountCode(DiscountCode discountCode, User user);
    DiscountCode findById(Long id);
}
