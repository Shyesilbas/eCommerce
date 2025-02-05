package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoBonusPointsException extends RuntimeException {
    public NoBonusPointsException(String s) {
        super(s);
    }
}
