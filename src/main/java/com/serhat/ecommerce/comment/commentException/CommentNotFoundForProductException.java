package com.serhat.ecommerce.comment.commentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundForProductException extends RuntimeException {
    public CommentNotFoundForProductException(String s) {
        super(s);
    }
}
