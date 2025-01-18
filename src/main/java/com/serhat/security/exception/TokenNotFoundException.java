package com.serhat.security.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String s) {
        super(s);
    }
}
