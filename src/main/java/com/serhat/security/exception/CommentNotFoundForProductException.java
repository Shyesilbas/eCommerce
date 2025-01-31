package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundForProductException extends RuntimeException {
    public CommentNotFoundForProductException(String s) {
        super(s);
    }
}
