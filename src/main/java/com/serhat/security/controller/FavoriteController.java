package com.serhat.security.controller;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.service.FavoritesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoritesService favoritesService;

    @GetMapping("/get-favorites")
    public ResponseEntity<Page<FavoriteProductDto>> getFavoritesByUser(HttpServletRequest request , Pageable pageable) {
        Page<FavoriteProductDto> favorites = favoritesService.getFavoritesByUser(request,pageable);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/add-favorite")
    public ResponseEntity<String> addFavorite(HttpServletRequest request, @RequestBody Map<String, Long> requestBody) {
        Long productId = requestBody.get("productId");
        favoritesService.addFavorite(request, productId);
        return ResponseEntity.ok("Product added to favorites successfully");
    }

    @DeleteMapping("/remove-favorite")
    public ResponseEntity<?> removeFavorite(HttpServletRequest request, @RequestParam Long productId) {
        try {
            favoritesService.removeFavorite(request, productId);
            return ResponseEntity.ok("Product removed from favorites successfully");
        } catch (Exception e) {
            log.error("Error removing favorite", e);
            return ResponseEntity.badRequest().body("Failed to remove favorite: " + e.getMessage());
        }
    }
}
