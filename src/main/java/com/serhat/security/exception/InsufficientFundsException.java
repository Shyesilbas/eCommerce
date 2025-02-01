package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String s) {
        super(s);
    }
}
