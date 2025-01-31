package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FavoriteProductNotFoundException extends RuntimeException {
    public FavoriteProductNotFoundException(String s) {
        super(s);
    }
}
