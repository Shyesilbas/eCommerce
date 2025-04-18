package com.serhat.ecommerce.order.orderException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String s) {
        super(s);
    }
}
