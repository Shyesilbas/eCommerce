package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundForUserException extends RuntimeException {
    public CommentNotFoundForUserException(String s) {
        super(s);
    }
}
