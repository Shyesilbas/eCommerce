package com.serhat.security.service;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.entity.Favorites;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.FavoritesRepository;
import com.serhat.security.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritesService {
    private final TokenInterface tokenInterface;
    private final FavoritesRepository favoritesRepository;
    private final ProductRepository productRepository;

    public List<FavoriteProductDto> getFavoritesByUser(HttpServletRequest servletRequest) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        return favoritesRepository.findByUser(user).stream()
                .map(this::convertToFavoriteProductDto)
                .collect(Collectors.toList());
    }

    private FavoriteProductDto convertToFavoriteProductDto(Favorites favorite) {
        Product product = favorite.getProduct();
        return FavoriteProductDto.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .brand(product.getBrand())
                .color(product.getColor())
                .category(product.getCategory())
                .averageRating(product.getAverageRating())
                .favorite_since(favorite.getAddedAt())
                .isFavorite(favorite.isFavorite())
                .build();
    }

    @Transactional
    public void addFavorite(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

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

    @Transactional
    public void removeFavorite(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Favorites favorite = favoritesRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favorite.setFavorite(false);
        favoritesRepository.delete(favorite);
        log.info("Product {} removed from favorites for user {}", productId, user.getUsername());
    }
}