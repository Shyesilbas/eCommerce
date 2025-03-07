package com.serhat.security.service.favorites;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.entity.Favorites;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.exception.FavoriteProductNotFoundException;
import com.serhat.security.component.mapper.FavoritesMapper;
import com.serhat.security.repository.FavoritesRepository;
import com.serhat.security.service.product.ProductService;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritesServiceImpl implements FavoritesService {
    private final UserService userService;
    private final FavoritesRepository favoritesRepository;
    private final ProductService productService;
    private final FavoritesMapper favoritesMapper;

    @Override
    public Page<FavoriteProductDto> getFavoritesByUser(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<Favorites> favoritesPage = favoritesRepository.findByUser(user, pageable);
        return favoritesPage.map(favoritesMapper::mapToFavoriteProductDto);
    }


    @Override
    @Transactional
    public void addFavorite(Long productId) {
        User user = userService.getAuthenticatedUser();
        Product product = productService.getProductById(productId);

        Optional<Favorites> existingFavorite = favoritesRepository.findByUserAndProduct(user, product);
        if (existingFavorite.isEmpty()) {
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
    public void removeFavorite(Long productId) {
        User user = userService.getAuthenticatedUser();
        Product product = productService.getProductById(productId);

        Favorites favorite = favoritesRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new FavoriteProductNotFoundException("Favorite not found"));

        favoritesRepository.delete(favorite);
        log.info("Product {} removed from favorites for user {}", productId, user.getUsername());
    }
}