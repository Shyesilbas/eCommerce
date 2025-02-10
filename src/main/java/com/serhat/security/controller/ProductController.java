package com.serhat.security.controller;

import com.serhat.security.dto.object.BestSellerProductDTO;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductPriceUpdate;
import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ProductRepository productRepository;

    @GetMapping("/totalCountByCategory")
    public ResponseEntity<Long> getTotalProductCountByCategory(@RequestParam Category category) {
        long totalCount = productService.totalProductCountByCategory(category);
        return ResponseEntity.ok(totalCount);
    }

    @GetMapping("/info/{productCode}")
    public ResponseEntity<ProductDto> productInfoByCode(@PathVariable String productCode){
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
        long totalCount = productService.totalProductCount();
        return ResponseEntity.ok(totalCount);
    }


    @GetMapping("/byCategory")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @RequestParam Category category,
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, page, size));
    }
    @GetMapping("/allProducts")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
       return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/allProductsWithoutPagination")
    public ResponseEntity<List<Product>> getAllProductsWithoutPagination() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/byPriceRange")
    public ResponseEntity<Page<ProductDto>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice,maxPrice,page,size));
    }

    @GetMapping("/byPriceAndCategory")
    public ResponseEntity<Page<ProductDto>> getProductsByPriceAndCategory(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductsByPriceAndCategory(minPrice,maxPrice,category,page,size));

    }


    @GetMapping("/byBrand")
    public ResponseEntity<Page<ProductDto>> getProductsByBrand(
            @RequestParam String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductsByBrand(brand,page,size));

    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-price")
    public ResponseEntity<ProductPriceUpdate> updatePrice(@RequestParam Long productId , @RequestParam BigDecimal price , HttpServletRequest servletRequest){
        return ResponseEntity.ok(productService.updateProductPrice(productId,price,servletRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-quantity")
    public ResponseEntity<ProductQuantityUpdate> updateQuantity(@RequestParam Long productId , @RequestParam int quantity , HttpServletRequest servletRequest){
        return ResponseEntity.ok(productService.updateProductQuantity(productId,quantity,servletRequest));
    }


    @GetMapping("/most-sellers")
    public ResponseEntity<List<BestSellerProductDTO>> mostSellers(@RequestParam  int size){
        return ResponseEntity.ok(productService.bestSellers(size));
    }
    @GetMapping("/most-sellers/category")
    public ResponseEntity<List<BestSellerProductDTO>> mostSellers(@RequestParam  Category category , @RequestParam int size){
        return ResponseEntity.ok(productService.bestSellersByCategory(category,size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addProduct")

    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request , HttpServletRequest servletRequest){
        return ResponseEntity.ok(productService.addProduct(request, servletRequest));
    }
}
