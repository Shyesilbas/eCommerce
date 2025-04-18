package com.serhat.ecommerce.user.userException.addressException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String s) {
        super(s);
    }
}
