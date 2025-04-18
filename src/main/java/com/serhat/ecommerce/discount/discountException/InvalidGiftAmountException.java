package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidGiftAmountException extends RuntimeException {
    public InvalidGiftAmountException(String s) {
        super(s);
    }
}
