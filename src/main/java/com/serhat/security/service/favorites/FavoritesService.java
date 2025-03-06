package com.serhat.security.service.favorites;

import com.serhat.security.dto.object.FavoriteProductDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritesService {
    Page<FavoriteProductDto> getFavoritesByUser(HttpServletRequest servletRequest, Pageable pageable);
    void addFavorite(HttpServletRequest servletRequest, Long productId);
    void removeFavorite(HttpServletRequest servletRequest, Long productId);
}
