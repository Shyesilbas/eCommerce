package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GiftCardNotFoundException extends RuntimeException {
    public GiftCardNotFoundException(String s) {
        super(s);
    }
}
