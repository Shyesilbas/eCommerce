package com.serhat.security.exception;

public class InvalidPasswordException extends Throwable{

    public InvalidPasswordException(String message) {
        super(message);
    }
}
