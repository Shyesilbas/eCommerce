package com.serhat.security.service;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.entity.Favorites;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.FavoritesRepository;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritesService {
    private final UserRepository userRepository;
    private final FavoritesRepository favoritesRepository;
    private final JwtUtil jwtUtil;
    private final ProductRepository productRepository;

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return token;
    }

    public List<FavoriteProductDto> getFavoritesByUser(HttpServletRequest servletRequest) {
        String token = extractTokenFromRequest(servletRequest);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        List<Favorites> favorites = favoritesRepository.findByUser(user);

        return favorites.stream()
                .map(this::convertToFavoriteProductDto)
                .collect(Collectors.toList());
    }

    private FavoriteProductDto convertToFavoriteProductDto(Favorites favorite) {
        return FavoriteProductDto.builder()
                .productCode(favorite.getProduct().getProductCode())
                .name(favorite.getProduct().getName())
                .price(favorite.getProduct().getPrice())
                .description(favorite.getProduct().getDescription())
                .brand(favorite.getProduct().getBrand())
                .color(favorite.getProduct().getColor())
                .category(favorite.getProduct().getCategory())
                .averageRating(favorite.getProduct().getAverageRating())
                .favorite_since(favorite.getAddedAt())
                .build();
    }

    public void addFavorite(HttpServletRequest servletRequest , Long productId) {
        String token = extractTokenFromRequest(servletRequest);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException(""));

        if (!favoritesRepository.existsByUserAndProduct(user, product)) {
            Favorites favorite = Favorites.builder()
                    .user(user)
                    .product(product)
                    .addedAt(LocalDate.now())
                    .build();
            favoritesRepository.save(favorite);
        }
    }

    public void removeFavorite(HttpServletRequest servletRequest, Long productId) {
        String token = extractTokenFromRequest(servletRequest);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        favoritesRepository.deleteByUserAndProduct(user, product);
    }

}
