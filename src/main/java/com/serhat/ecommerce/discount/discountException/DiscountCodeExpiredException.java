package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiscountCodeExpiredException extends RuntimeException {
    public DiscountCodeExpiredException(String s) {
        super(s);
    }
}
