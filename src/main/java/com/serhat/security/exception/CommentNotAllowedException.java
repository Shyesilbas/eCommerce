package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotAllowedException extends RuntimeException {
    public CommentNotAllowedException(String s) {
        super(s);
    }
}
