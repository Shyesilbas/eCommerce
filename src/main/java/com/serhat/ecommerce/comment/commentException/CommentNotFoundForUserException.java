package com.serhat.ecommerce.comment.commentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundForUserException extends RuntimeException {
    public CommentNotFoundForUserException(String s) {
        super(s);
    }
}
