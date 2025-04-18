package com.serhat.ecommerce.user.userException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SamePlanRequestException extends RuntimeException {
    public SamePlanRequestException(String s) {
        super(s);
    }
}
