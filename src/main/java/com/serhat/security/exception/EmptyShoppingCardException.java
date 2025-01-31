package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyShoppingCardException extends RuntimeException {
    public EmptyShoppingCardException(String s) {
        super(s);
    }
}
