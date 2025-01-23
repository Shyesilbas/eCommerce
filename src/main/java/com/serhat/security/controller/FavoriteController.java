package com.serhat.security.controller;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.service.FavoritesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoritesService favoritesService;

    @GetMapping("/get-favorites")
    public ResponseEntity<List<FavoriteProductDto>> getFavoritesByUser(HttpServletRequest request) {
        List<FavoriteProductDto> favorites = favoritesService.getFavoritesByUser(request);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/add-favorite")
    public ResponseEntity<String> addFavorite(HttpServletRequest request, @RequestParam Long productId) {
        favoritesService.addFavorite(request, productId);
        return ResponseEntity.ok("Product added to favorites successfully");
    }

    @DeleteMapping("/remove-favorite")
    public ResponseEntity<String> removeFavorite(HttpServletRequest request, @RequestParam Long productId) {
        favoritesService.removeFavorite(request, productId);
        return ResponseEntity.ok("Product removed from favorites successfully");
    }
}
