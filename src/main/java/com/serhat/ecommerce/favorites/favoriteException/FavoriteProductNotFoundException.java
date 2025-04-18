package com.serhat.ecommerce.favorites.favoriteException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FavoriteProductNotFoundException extends RuntimeException {
    public FavoriteProductNotFoundException(String s) {
        super(s);
    }
}
