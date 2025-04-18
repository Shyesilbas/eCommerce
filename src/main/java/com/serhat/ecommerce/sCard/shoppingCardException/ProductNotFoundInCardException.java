package com.serhat.ecommerce.sCard.shoppingCardException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductNotFoundInCardException extends RuntimeException {
    public ProductNotFoundInCardException(String s) {
        super(s);
    }
}
