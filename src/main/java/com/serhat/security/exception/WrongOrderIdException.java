package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WrongOrderIdException extends RuntimeException {
    public WrongOrderIdException(String s) {
        super(s);
    }
}
