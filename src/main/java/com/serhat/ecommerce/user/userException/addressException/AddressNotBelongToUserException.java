package com.serhat.ecommerce.user.userException.addressException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressNotBelongToUserException extends RuntimeException {
    public AddressNotBelongToUserException(String s) {
        super(s);
    }
}
