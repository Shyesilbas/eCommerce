package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CouponAlreadyUsedException extends RuntimeException {
    public CouponAlreadyUsedException(String s) {
        super(s);
    }
}
