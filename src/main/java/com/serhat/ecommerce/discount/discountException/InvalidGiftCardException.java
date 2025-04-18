package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidGiftCardException extends RuntimeException {
    public InvalidGiftCardException(String s) {
        super(s);
    }
}
