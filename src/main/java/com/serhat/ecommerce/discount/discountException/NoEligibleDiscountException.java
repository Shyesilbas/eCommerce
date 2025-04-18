package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoEligibleDiscountException extends RuntimeException {
    public NoEligibleDiscountException(String s) {
        super(s);
    }
}
