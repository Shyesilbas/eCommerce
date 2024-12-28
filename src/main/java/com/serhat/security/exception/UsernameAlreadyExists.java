package com.serhat.security.exception;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String s) {
        super(s);
    }
}
