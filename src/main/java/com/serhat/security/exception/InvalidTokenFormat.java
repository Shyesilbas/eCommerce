package com.serhat.security.exception;

public class InvalidTokenFormat extends RuntimeException {
    public InvalidTokenFormat(String s) {
        super(s);
    }
}
