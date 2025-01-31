package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressNotBelongToUserException extends RuntimeException {
    public AddressNotBelongToUserException(String s) {
        super(s);
    }
}
