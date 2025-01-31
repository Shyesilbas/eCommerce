package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoOrderException extends RuntimeException {
    public NoOrderException(String s) {
        super(s);
    }
}
