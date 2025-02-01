package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String s) {
        super(s);
    }
}
