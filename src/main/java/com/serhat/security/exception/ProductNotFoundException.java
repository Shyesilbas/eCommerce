package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String s) {
        super(s);
    }
}
