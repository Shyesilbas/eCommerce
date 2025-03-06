package com.serhat.security.controller;

import com.serhat.security.dto.object.BestSellerProductDTO;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductPriceUpdate;
import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.service.inventory.InventoryService;
import com.serhat.security.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final InventoryService inventoryService;

    @GetMapping("/totalCountByCategory")
    public ResponseEntity<Long> getTotalProductCountByCategory(@RequestParam Category category) {
        return ResponseEntity.ok(productService.totalProductCountByCategory(category));
    }

    @GetMapping("/info/{productCode}")
    public ResponseEntity<ProductDto> productInfoByCode(@PathVariable String productCode) {
        return ResponseEntity.ok(productService.productInfo(productCode));
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .toList();
    }

    @GetMapping("/totalCount")
    public ResponseEntity<Long> getTotalProductCount() {
        return ResponseEntity.ok(productService.totalProductCount());
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDto>> getFilteredProducts(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getFilteredProducts(minPrice, maxPrice, category, brand, page, size));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-price")
    public ResponseEntity<ProductPriceUpdate> updatePrice(
            @RequestParam Long productId,
            @RequestParam BigDecimal price,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(productService.updateProductPrice(productId, price, servletRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-quantity")
    public ResponseEntity<ProductQuantityUpdate> updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(inventoryService.updateProductQuantity(productId, quantity, servletRequest));
    }

    @GetMapping("/most-sellers")
    public ResponseEntity<List<BestSellerProductDTO>> mostSellers(
            @RequestParam(required = false) Category category,
            @RequestParam int size) {
        if (category != null) {
            return ResponseEntity.ok(productService.bestSellersByCategory(category, size));
        } else {
            return ResponseEntity.ok(productService.bestSellers(size));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(
            @RequestBody ProductRequest request,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(productService.addProduct(request, servletRequest));
    }
}