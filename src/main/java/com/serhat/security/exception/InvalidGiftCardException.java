package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidGiftCardException extends RuntimeException {
    public InvalidGiftCardException(String s) {
        super(s);
    }
}
