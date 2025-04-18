package com.serhat.ecommerce.order.orderException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WrongOrderIdException extends RuntimeException {
    public WrongOrderIdException(String s) {
        super(s);
    }
}
