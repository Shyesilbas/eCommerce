package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String s) {
        super(s);
    }
}
