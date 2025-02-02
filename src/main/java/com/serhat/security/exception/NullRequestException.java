package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NullRequestException extends RuntimeException
{
    public NullRequestException(String s) {
        super(s);
    }
}
