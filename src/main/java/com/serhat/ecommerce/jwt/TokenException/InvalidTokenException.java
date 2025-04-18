package com.serhat.ecommerce.jwt.TokenException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String s) {
        super(s);
    }
}
