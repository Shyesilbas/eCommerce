package com.serhat.ecommerce.auth.authException;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String s) {
        super(s);
    }
}
