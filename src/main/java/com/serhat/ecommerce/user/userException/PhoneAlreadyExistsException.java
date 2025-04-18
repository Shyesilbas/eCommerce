package com.serhat.ecommerce.user.userException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException(String s) {
        super(s);
    }
}
