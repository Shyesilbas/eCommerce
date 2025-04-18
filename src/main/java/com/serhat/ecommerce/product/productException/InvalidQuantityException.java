package com.serhat.ecommerce.product.productException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String s) {
        super(s);
    }
}
