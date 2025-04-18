package com.serhat.ecommerce.order.orderException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String s) {
        super(s);
    }
}
