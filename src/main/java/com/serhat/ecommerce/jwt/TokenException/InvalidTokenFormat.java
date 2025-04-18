package com.serhat.ecommerce.jwt.TokenException;

public class InvalidTokenFormat extends RuntimeException {
    public InvalidTokenFormat(String s) {
        super(s);
    }
}
