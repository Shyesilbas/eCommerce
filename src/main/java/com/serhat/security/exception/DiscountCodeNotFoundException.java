package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiscountCodeNotFoundException extends RuntimeException {
    public DiscountCodeNotFoundException(String s) {
        super(s);
    }
}
