package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidGiftAmountException extends RuntimeException {
    public InvalidGiftAmountException(String s) {
        super(s);
    }
}
