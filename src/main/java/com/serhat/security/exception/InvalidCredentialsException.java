package com.serhat.security.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String s) {
        super(s);
    }
}
