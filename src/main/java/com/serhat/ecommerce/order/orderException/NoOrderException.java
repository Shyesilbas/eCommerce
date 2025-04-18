package com.serhat.ecommerce.order.orderException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoOrderException extends RuntimeException {
    public NoOrderException(String s) {
        super(s);
    }
}
