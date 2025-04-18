package com.serhat.ecommerce.user.userException;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String s) {
        super(s);
    }
}
