package com.serhat.security.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String s) {
        super(s);
    }
}
