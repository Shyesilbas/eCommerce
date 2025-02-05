package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException(String s) {
        super(s);
    }
}
