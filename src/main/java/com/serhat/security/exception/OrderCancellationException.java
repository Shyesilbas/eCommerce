package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderCancellationException extends RuntimeException {
    public OrderCancellationException(String s) {
        super(s);
    }
}
