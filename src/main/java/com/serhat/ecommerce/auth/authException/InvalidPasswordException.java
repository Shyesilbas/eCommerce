package com.serhat.ecommerce.auth.authException;

public class InvalidPasswordException extends Throwable{

    public InvalidPasswordException(String message) {
        super(message);
    }
}
