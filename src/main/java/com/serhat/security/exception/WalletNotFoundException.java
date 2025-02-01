package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String s) {
        super(s);
    }
}
