package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String s) {
        super(s);
    }
}
