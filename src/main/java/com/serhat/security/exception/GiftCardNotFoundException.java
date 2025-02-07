package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GiftCardNotFoundException extends RuntimeException {
    public GiftCardNotFoundException(String s) {
        super(s);
    }
}
