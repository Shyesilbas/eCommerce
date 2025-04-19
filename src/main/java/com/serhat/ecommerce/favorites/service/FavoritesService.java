package com.serhat.ecommerce.favorites.service;

import com.serhat.ecommerce.favorites.dto.FavoriteProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritesService {
    Page<FavoriteProductDto> getFavoritesByUser( Pageable pageable);
    void addFavorite( Long productId);
    void removeFavorite( Long productId);
}
