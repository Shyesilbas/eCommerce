package com.serhat.ecommerce.product.productException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String s) {
        super(s);
    }
}
