package com.serhat.ecommerce.order.orderException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderAlreadyCanceledException extends RuntimeException {
    public OrderAlreadyCanceledException(String s) {
        super(s);
    }
}
