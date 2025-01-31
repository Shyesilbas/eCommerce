package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String s) {
        super(s);
    }
}
