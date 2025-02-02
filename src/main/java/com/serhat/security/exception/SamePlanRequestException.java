package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SamePlanRequestException extends RuntimeException {
    public SamePlanRequestException(String s) {
        super(s);
    }
}
