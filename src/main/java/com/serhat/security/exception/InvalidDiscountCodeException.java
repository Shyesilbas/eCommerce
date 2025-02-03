package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidDiscountCodeException extends RuntimeException {
    public InvalidDiscountCodeException(String s) {
        super(s);
    }
}
