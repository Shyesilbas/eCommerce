package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlreadyHasWalletException extends RuntimeException {
    public AlreadyHasWalletException(String s) {
        super(s);
    }
}
