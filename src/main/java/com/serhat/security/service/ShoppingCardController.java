package com.serhat.security.service;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.object.FavoriteProductDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopping-card")
@RequiredArgsConstructor
@Slf4j
public class ShoppingCardController {
    private final ShoppingCardService service;

    @GetMapping("/get-items")
    public ResponseEntity<List<CardProductDto>> getShoppingCardItems(HttpServletRequest request) {
       return ResponseEntity.ok(service.getShoppingCardByUser(request));
    }

    @GetMapping("/total-price")
    public ResponseEntity<BigDecimal> totalPriceOnCard(HttpServletRequest request){
        return ResponseEntity.ok(service.totalPrice(request));
    }

    @GetMapping("/total-product")
    public ResponseEntity<Long> totalProductsOnCard(HttpServletRequest request){
        return ResponseEntity.ok(service.totalProduct(request));
    }

    @PostMapping("/add-to-card")
    public ResponseEntity<String> addToFavorite(HttpServletRequest request, @RequestBody Map<String, Long> requestBody) {
        Long productId = requestBody.get("productId");
        service.addToCard(request, productId);
        return ResponseEntity.ok("Product added to card successfully");
    }

    @DeleteMapping("/remove-from-card")
    public ResponseEntity<?> removeFavorite(HttpServletRequest request, @RequestParam Long productId) {
        try {
            service.removeFromCard(request, productId);
            return ResponseEntity.ok("Product removed from card successfully");
        } catch (Exception e) {
            log.error("Error removing favorite", e);
            return ResponseEntity.badRequest().body("Failed to remove item: " + e.getMessage());
        }
    }


}
