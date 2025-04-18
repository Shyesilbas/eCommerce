package com.serhat.ecommerce.product.productException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String s) {
        super(s);
    }
}
