package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyFavoriteListException extends RuntimeException
{
    public EmptyFavoriteListException(String s) {
        super(s);
    }
}
