package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiscountCodeNotFoundException extends RuntimeException {
    public DiscountCodeNotFoundException(String s) {
        super(s);
    }
}
