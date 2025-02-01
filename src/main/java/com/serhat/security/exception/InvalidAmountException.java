package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String s) {
        super(s);
    }
}
