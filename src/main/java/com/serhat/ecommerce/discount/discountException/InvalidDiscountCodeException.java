package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidDiscountCodeException extends RuntimeException {
    public InvalidDiscountCodeException(String s) {
        super(s);
    }
}
