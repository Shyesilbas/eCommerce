package com.serhat.security.service;

import com.serhat.security.dto.object.BestSellerProductDTO;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductPriceUpdate;
import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.StockStatus;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    private void validateAdminRole(HttpServletRequest request) {
        if (jwtUtil.extractRole(jwtUtil.getTokenFromAuthorizationHeader(request)) != Role.ADMIN) {
            throw new RuntimeException("Only ADMIN users can perform this action.");
        }
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productId));
    }

    public ProductQuantityUpdate updateProductQuantity(Long productId, int quantity, HttpServletRequest request) {
        validateAdminRole(request);
        Product product = getProductById(productId);

        if(quantity<0){
            throw new InvalidAmountException("Product quantity cannot be negative");
        }

        product.setQuantity(quantity);
        productRepository.save(product);
        log.info("Product quantity updated: {} -> {}", productId, quantity);

        return new ProductQuantityUpdate(
                product.getName(),
                product.getProductCode(),
                quantity
        );
    }

    public ProductPriceUpdate updateProductPrice(Long productId, BigDecimal price, HttpServletRequest request) {
        validateAdminRole(request);
        Product product = getProductById(productId);



        if(price.compareTo(BigDecimal.ZERO)<0){
            throw new InvalidAmountException("Price cannot be negative");
        }

        product.setPrice(price);
        productRepository.save(product);
        log.info("Product price updated: {} -> {}", productId, price);

        return new ProductPriceUpdate(
                product.getName(),
                product.getProductCode(),
                price
        );
    }

    public long totalProductCountByCategory(Category category) {
        return productRepository.countByCategory(category);
    }


    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId"));
        return productRepository.findAll(pageable);
    }

    public List<BestSellerProductDTO> bestSellersByCategory(Category category, int size) {
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);

        List<Long> productIds = deliveredOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .filter(product -> product.getCategory() == category)
                .map(Product::getProductId)
                .toList();

        Map<Long, Long> productSalesCount = productIds.stream()
                .collect(Collectors.groupingBy(productId -> productId, Collectors.counting()));

        List<Long> sortedProductIds = productSalesCount.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .limit(size)
                .collect(Collectors.toList());

        List<Product> bestSellingProducts = productRepository.findAllById(sortedProductIds);

        return bestSellingProducts.stream()
                .map(product -> new BestSellerProductDTO(
                        product.getName(),
                        product.getOriginOfCountry(),
                        product.getProductCode(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getBrand(),
                        product.getColor(),
                        product.getCategory()))
                .collect(Collectors.toList());
    }


    public List<BestSellerProductDTO> bestSellers(int size) {
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);

        List<Long> productIds = deliveredOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(orderItem -> orderItem.getProduct().getProductId())
                .toList();

        Map<Long, Long> productSalesCount = productIds.stream()
                .collect(Collectors.groupingBy(productId -> productId, Collectors.counting()));

        List<Long> sortedProductIds = productSalesCount.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .limit(size)
                .collect(Collectors.toList());

        List<Product> bestSellingProducts = productRepository.findAllById(sortedProductIds);

        return bestSellingProducts.stream()
                .map(product -> new BestSellerProductDTO(
                        product.getName(),
                        product.getOriginOfCountry(),
                        product.getProductCode(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getBrand(),
                        product.getColor(),
                        product.getCategory()))
                .collect(Collectors.toList());
    }



    public long totalProductCount(){
        return productRepository.count();
    }

    public ProductDto productInfo(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productCode));

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

    public ProductDto productInfoById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productId));

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
        String jwtToken = jwtUtil.getTokenFromAuthorizationHeader(request);

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

    public Page<Product> getProductsByCategory(Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductsByPriceAndCategory(BigDecimal minPrice, BigDecimal maxPrice, Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByPriceBetweenAndCategory(minPrice, maxPrice, category, pageable);
    }

    public Page<Product> getProductsByBrand(String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByBrandIgnoreCase(brand, pageable);
    }
}