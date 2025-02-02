package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundForBrandException extends RuntimeException {
    public CommentNotFoundForBrandException(String s) {
        super(s);
    }
}
