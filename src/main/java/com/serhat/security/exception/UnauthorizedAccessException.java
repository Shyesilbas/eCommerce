package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String s) {
        super(s);
    }
}
