package com.serhat.security.controller;

import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/totalCountByCategory")
    public ResponseEntity<Long> getTotalProductCountByCategory(@RequestParam Category category) {
        long totalCount = productService.totalProductCountByCategory(category);
        return ResponseEntity.ok(totalCount);
    }

    @GetMapping("/info/{productCode}")
    public ResponseEntity<ProductDto> productInfoByCode(@PathVariable String productCode){
        return ResponseEntity.ok(productService.productInfo(productCode));
    }

    @GetMapping("/info/id/{productId}")
    public ResponseEntity<ProductDto> productInfoById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.productInfoById(productId));
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::toDisplayName)
                .toList();
    }


    @GetMapping("/totalCount")
    public ResponseEntity<Long> getTotalProductCount() {
        long totalCount = productService.totalProductCount();
        return ResponseEntity.ok(totalCount);
    }


    @GetMapping("/byCategory")
    public Page<Product> getProductsByCategory(
            @RequestParam Category category,
            @RequestParam int page,
            @RequestParam int size) {
        return productService.getProductsByCategory(category, page, size);
    }
    @GetMapping("/allProducts")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request , HttpServletRequest servletRequest){
        return ResponseEntity.ok(productService.addProduct(request, servletRequest));
    }
}
