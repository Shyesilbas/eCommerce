package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String s) {
        super(s);
    }
}
