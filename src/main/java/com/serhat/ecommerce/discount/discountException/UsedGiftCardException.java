package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsedGiftCardException extends RuntimeException {
    public UsedGiftCardException(String s) {
        super(s);
    }
}
