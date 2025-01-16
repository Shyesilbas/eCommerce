package com.serhat.security.service;

import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.StockStatus;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;


    public ProductDto productInfo(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found for: " + productCode));

        return new ProductDto(
                product.getProductId(),
                product.getName(),
                product.getOriginOfCountry(),
                product.getProductCode(),
                product.getDescription(),
                product.getPrice(),
                product.getBrand(),
                product.getAverageRating(),
                product.getStockStatus(),
                product.getColor(),
                product.getQuantity(),
                product.getCategory()
        );
    }


    public ProductResponse addProduct(ProductRequest productRequest, HttpServletRequest request) {
        String jwtToken = jwtUtil.getTokenFromCookie(request);

        Role role =  jwtUtil.extractRole(jwtToken);

        if (role != Role.ADMIN) {
            log.warn("Unauthorized access attempt to add product by role: {}", role);
            throw new RuntimeException("Only ADMIN users can add products.");
        }

        Product product = Product.builder()
                .name(productRequest.name())
                .originOfCountry(productRequest.originOfCountry())
                .productCode(productRequest.productCode())
                .description(productRequest.description())
                .price(productRequest.price())
                .brand(productRequest.brand())
                .averageRating(productRequest.averageRating())
                .stockStatus(StockStatus.valueOf(productRequest.stockStatus()))
                .color(productRequest.color())
                .quantity(productRequest.quantity())
                .category(Category.valueOf(productRequest.category()))
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully: {}", savedProduct.getProductCode());

        return ProductResponse.builder()
                .productId(savedProduct.getProductId())
                .name(savedProduct.getName())
                .productCode(savedProduct.getProductCode())
                .message("Product added successfully")
                .build();
    }
}