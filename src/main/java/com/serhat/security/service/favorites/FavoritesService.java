package com.serhat.security.service.favorites;

import com.serhat.security.dto.object.FavoriteProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritesService {
    Page<FavoriteProductDto> getFavoritesByUser( Pageable pageable);
    void addFavorite( Long productId);
    void removeFavorite( Long productId);
}
