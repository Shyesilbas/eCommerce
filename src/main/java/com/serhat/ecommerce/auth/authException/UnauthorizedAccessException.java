package com.serhat.ecommerce.auth.authException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String s) {
        super(s);
    }
}
