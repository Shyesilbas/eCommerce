package com.serhat.ecommerce.order.orderException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderCancellationException extends RuntimeException {
    public OrderCancellationException(String s) {
        super(s);
    }
}
