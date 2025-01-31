package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoNotificationsFoundException extends RuntimeException
{
    public NoNotificationsFoundException(String s) {
        super(s);
    }
}
