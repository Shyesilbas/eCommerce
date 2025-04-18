package com.serhat.ecommerce.discount.discountException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoBonusPointsException extends RuntimeException {
    public NoBonusPointsException(String s) {
        super(s);
    }
}
