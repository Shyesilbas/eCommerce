package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsedGiftCardException extends RuntimeException {
    public UsedGiftCardException(String s) {
        super(s);
    }
}
