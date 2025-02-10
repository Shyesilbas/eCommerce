package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderAlreadyCanceledException extends RuntimeException {
    public OrderAlreadyCanceledException(String s) {
        super(s);
    }
}
