package com.serhat.security.service.favorites;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.entity.Favorites;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.exception.EmptyFavoriteListException;
import com.serhat.security.exception.FavoriteProductNotFoundException;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.component.mapper.FavoritesMapper;
import com.serhat.security.repository.FavoritesRepository;
import com.serhat.security.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritesServiceImpl implements FavoritesService {
    private final TokenInterface tokenInterface;
    private final FavoritesRepository favoritesRepository;
    private final ProductRepository productRepository;
    private final FavoritesMapper favoritesMapper;

    @Override
    public Page<FavoriteProductDto> getFavoritesByUser(HttpServletRequest servletRequest, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(servletRequest);

        Page<Favorites> favoritesPage = favoritesRepository.findByUser(user, pageable);

        if (favoritesPage.isEmpty()) {
            throw new EmptyFavoriteListException("Favorite list is empty");
        }

        return favoritesPage.map(favoritesMapper::mapToFavoriteProductDto);
    }


    @Override
    @Transactional
    public void addFavorite(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!favoritesRepository.existsByUserAndProduct(user, product)) {
            Favorites favorite = Favorites.builder()
                    .user(user)
                    .product(product)
                    .addedAt(LocalDate.now())
                    .isFavorite(true)
                    .build();
            favoritesRepository.save(favorite);
            log.info("Product {} added to favorites for user {}", productId, user.getUsername());
        }
    }

    @Override
    @Transactional
    public void removeFavorite(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Favorites favorite = favoritesRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new FavoriteProductNotFoundException("Favorite not found"));

        favorite.setFavorite(false);
        favoritesRepository.delete(favorite);
        log.info("Product {} removed from favorites for user {}", productId, user.getUsername());
    }
}