package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String s) {
        super(s);
    }
}
