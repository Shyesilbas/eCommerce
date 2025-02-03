package com.serhat.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CouponAlreadyUsedException extends RuntimeException {
    public CouponAlreadyUsedException(String s) {
        super(s);
    }
}
