package com.serhat.security.controller;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.dto.response.AddedToCardResponse;
import com.serhat.security.dto.response.QuantityUpdateResponse;
import com.serhat.security.dto.response.TotalInfo;
import com.serhat.security.service.ShoppingCardService;
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

    @GetMapping("/totalInfos")
    public TotalInfo getTotalInfo(HttpServletRequest request) {
        return service.calculateTotalInfo(request);
    }

    @PostMapping("/add-to-card")
    public ResponseEntity<AddedToCardResponse> addToCard(HttpServletRequest request, @RequestBody Map<String, Long> requestBody) {
        Long productId = requestBody.get("productId");
        return ResponseEntity.ok(service.addToCard(request, productId));
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<QuantityUpdateResponse> updateQuantity(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(service.handleQuantity(request, productId, quantity));
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
