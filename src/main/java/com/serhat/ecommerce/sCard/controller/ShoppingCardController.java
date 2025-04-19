package com.serhat.ecommerce.sCard.controller;

import com.serhat.ecommerce.product.dto.CardProductDto;
import com.serhat.ecommerce.sCard.dto.AddedToCardResponse;
import com.serhat.ecommerce.product.dto.QuantityUpdateResponse;
import com.serhat.ecommerce.sCard.dto.ShoppingCardInfo;
import com.serhat.ecommerce.sCard.service.ShoppingCartService;
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
    private final ShoppingCartService service;

    @GetMapping("/get-items")
    public ResponseEntity<List<CardProductDto>> getShoppingCardItems() {
       return ResponseEntity.ok(service.getItems());
    }

    @GetMapping("/total-price")
    public ResponseEntity<BigDecimal> totalPriceOnCard(){
        return ResponseEntity.ok(service.totalPrice());
    }

    @GetMapping("/total-product")
    public ResponseEntity<Long> totalProductsOnCard(){
        return ResponseEntity.ok(service.totalProduct());
    }

    @GetMapping("/card-details")
    public ResponseEntity<ShoppingCardInfo> getShoppingCardTotalInfo() {
        ShoppingCardInfo ShoppingCardInfo = service.getShoppingCardTotalInfo();
        return ResponseEntity.ok(ShoppingCardInfo);
    }

    @PostMapping("/add-to-card")
    public ResponseEntity<AddedToCardResponse> addToCard( @RequestBody Map<String, Long> RequestBody) {
        Long productId = RequestBody.get("productId");
        return ResponseEntity.ok(service.addToCard(productId));
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<QuantityUpdateResponse> updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(service.handleQuantity(productId, quantity));
    }


    @DeleteMapping("/remove-from-card")
    public ResponseEntity<?> removeFavorite( @RequestParam Long productId) {
        try {
            service.removeFromCard(productId);
            return ResponseEntity.ok("Product removed from card successfully");
        } catch (Exception e) {
            log.error("Error removing favorite", e);
            return ResponseEntity.badRequest().body("Failed to remove item: " + e.getMessage());
        }
    }


}
