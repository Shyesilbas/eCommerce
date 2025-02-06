package com.serhat.security.service;

import com.serhat.security.dto.object.BestSellerProductDTO;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductPriceUpdate;
import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.PriceHistory;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.StockStatus;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.mapper.ProductMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.PriceHistoryRepository;
import com.serhat.security.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final JwtUtil jwtUtil;
    private final ProductMapper productMapper;
    private final PriceHistoryService priceHistoryService;
    private final StockAlertService stockAlertService;

    private void validateAdminRole(HttpServletRequest request) {
        if (jwtUtil.extractRole(jwtUtil.getTokenFromAuthorizationHeader(request)) != Role.ADMIN) {
            throw new RuntimeException("Only ADMIN users can perform this action.");
        }
    }
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productId));
    }

    public void manageStockAlerts(Long productId) {
        stockAlertService.handleStockAlert(productId);
    }
    public ProductQuantityUpdate updateProductQuantity(Long productId, int quantity, HttpServletRequest request) {
        validateAdminRole(request);
        Product product = getProductById(productId);

        if (quantity < 0) {
            throw new InvalidAmountException("Product quantity cannot be negative");
        }
        if(quantity == 0){
            product.setStockStatus(StockStatus.OUT_OF_STOCKS);
        }
        if(quantity>0){
            product.setStockStatus(StockStatus.AVAILABLE);
        }

        product.setQuantity(quantity);
        productRepository.save(product);
        log.info("Product quantity updated: id {} -> {}", productId, quantity);
        manageStockAlerts(product.getProductId());
        return new ProductQuantityUpdate(product.getName(), product.getProductCode(), quantity);
    }

    public ProductPriceUpdate updateProductPrice(Long productId, BigDecimal price, HttpServletRequest request) {
        validateAdminRole(request);
        Product product = getProductById(productId);

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Price cannot be negative");
        }

        BigDecimal oldPrice = product.getPrice();
        double changePercentage = calculateChangePercentage(oldPrice, price);
        double totalChangePercentage = calculateTotalChangePercentage(product.getProductId(), price);

        priceHistoryService.createAndSavePriceHistory(product, oldPrice, price, changePercentage, totalChangePercentage);

        product.setPrice(price);
        productRepository.save(product);
        log.info("Product price updated: {} -> {}", productId, price);

        return new ProductPriceUpdate(product.getName(), product.getProductCode(), price);
    }

    private double calculateChangePercentage(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return newPrice.subtract(oldPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private double calculateTotalChangePercentage(Long productId, BigDecimal currentPrice) {
        PriceHistory firstPriceHistory = priceHistoryRepository.findFirstByProduct_ProductIdOrderByChangeDateAsc(productId);
        BigDecimal firstPrice = firstPriceHistory == null ? currentPrice : firstPriceHistory.getOldPrice();
        return calculateChangePercentage(firstPrice, currentPrice);
    }

    public long totalProductCountByCategory(Category category) {
        return productRepository.countByCategory(category);
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId"));
        return productRepository.findAll(pageable);
    }

    public List<BestSellerProductDTO> bestSellersByCategory(Category category, int size) {
        return getBestSellers(size, category);
    }

    public List<BestSellerProductDTO> bestSellers(int size) {
        return getBestSellers(size, null);
    }

    private List<BestSellerProductDTO> getBestSellers(int size, Category category) {
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);
        List<Long> productIds = deliveredOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .filter(product -> category == null || product.getCategory() == category)
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
                .map(productMapper::mapToBestSeller)
                .collect(Collectors.toList());
    }

    public long totalProductCount() {
        return productRepository.count();
    }

    public ProductDto productInfo(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productCode));
        return productMapper.mapToProductDto(product);
    }

    public ProductDto productInfoById(Long productId) {
        return productMapper.mapToProductDto(getProductById(productId));
    }

    public ProductResponse addProduct(ProductRequest productRequest, HttpServletRequest request) {
        validateAdminRole(request);

        Product product = productMapper.mapToProduct(productRequest);
        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully: {}", savedProduct.getProductCode());

        return ProductResponse.builder()
                .productId(savedProduct.getProductId())
                .name(savedProduct.getName())
                .productCode(savedProduct.getProductCode())
                .message("Product added successfully")
                .build();
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    public Page<Product> getProductsByCategory(Category category, int page, int size) {
        return productRepository.findByCategory(category, createPageable(page, size));
    }

    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, createPageable(page, size));
    }

    public Page<Product> getProductsByPriceAndCategory(BigDecimal minPrice, BigDecimal maxPrice, Category category, int page, int size) {
        return productRepository.findByPriceBetweenAndCategory(minPrice, maxPrice, category, createPageable(page, size));
    }

    public Page<Product> getProductsByBrand(String brand, int page, int size) {
        return productRepository.findByBrandIgnoreCase(brand, createPageable(page, size));
    }

}
