package com.serhat.ecommerce.comment.commentException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundForBrandException extends RuntimeException {
    public CommentNotFoundForBrandException(String s) {
        super(s);
    }
}
