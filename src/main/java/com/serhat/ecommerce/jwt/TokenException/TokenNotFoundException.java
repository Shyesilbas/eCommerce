package com.serhat.ecommerce.jwt.TokenException;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String s) {
        super(s);
    }
}
