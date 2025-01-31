package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String s) {
        super(s);
    }
}
