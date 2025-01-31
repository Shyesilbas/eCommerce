package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductNotFoundInCardException extends RuntimeException {
    public ProductNotFoundInCardException(String s) {
        super(s);
    }
}
