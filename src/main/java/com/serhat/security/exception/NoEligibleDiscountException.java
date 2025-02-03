package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoEligibleDiscountException extends RuntimeException {
    public NoEligibleDiscountException(String s) {
        super(s);
    }
}
