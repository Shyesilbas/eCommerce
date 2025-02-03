package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiscountCodeExpiredException extends RuntimeException {
    public DiscountCodeExpiredException(String s) {
        super(s);
    }
}
