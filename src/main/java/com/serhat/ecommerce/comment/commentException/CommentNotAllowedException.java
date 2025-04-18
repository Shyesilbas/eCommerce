package com.serhat.ecommerce.comment.commentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotAllowedException extends RuntimeException {
    public CommentNotAllowedException(String s) {
        super(s);
    }
}
