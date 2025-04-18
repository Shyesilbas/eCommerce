package com.serhat.ecommerce.favorites.favoriteException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyFavoriteListException extends RuntimeException
{
    public EmptyFavoriteListException(String s) {
        super(s);
    }
}
