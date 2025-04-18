package com.serhat.ecommerce.sCard.shoppingCardException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyShoppingCardException extends RuntimeException {
    public EmptyShoppingCardException(String s) {
        super(s);
    }
}
