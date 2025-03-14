package com.serhat.security.service.product;

import com.serhat.security.dto.object.BestSellerProductDTO;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.PriceChangeInfo;
import com.serhat.security.dto.response.ProductPriceUpdate;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.StockStatus;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.InvalidQuantityException;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.component.mapper.ProductMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductMapper productMapper;
    private final PriceHistoryService priceHistoryService;
    private final TokenInterface tokenInterface;


    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productId));
    }
    @Override
    public ProductPriceUpdate updateProductPrice(Long productId, BigDecimal price, HttpServletRequest request) {
        tokenInterface.validateRole(request, Role.ADMIN, Role.MANAGER);
        Product product = getProductById(productId);

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Price cannot be negative");
        }

        BigDecimal oldPrice = product.getPrice();
        PriceChangeInfo priceChangeInfo = priceHistoryService.calculatePriceChanges(product.getProductId(), oldPrice, price);

        priceHistoryService.createAndSavePriceHistory(product, oldPrice, price,
                priceChangeInfo.changePercentage(), priceChangeInfo.totalChangePercentage());

        product.setPrice(price);
        productRepository.save(product);
        log.info("Product price updated: {} -> {}", productId, price);

        return new ProductPriceUpdate(product.getName(), product.getProductCode(), price);
    }

    @Override
    public long totalProductCountByCategory(Category category) {
        return productRepository.countByCategory(category);
    }

    @Override
    public List<BestSellerProductDTO> bestSellersByCategory(Category category, int size) {
        return getBestSellers(size, category);
    }

    @Override
    public List<BestSellerProductDTO> bestSellers(int size) {
        return getBestSellers(size, null);
    }

    private List<BestSellerProductDTO> getBestSellers(int size, Category category) {
        List<Order> deliveredOrders = orderRepository.findByStatusNot(OrderStatus.REFUNDED);

        Map<Long, Long> productSalesCount = deliveredOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .filter(product -> category == null || product.getCategory() == category)
                .collect(Collectors.groupingBy(Product::getProductId, Collectors.counting()));

        return productSalesCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(size)
                .map(entry -> productRepository.findById(entry.getKey()).orElseThrow())
                .map(productMapper::mapToBestSeller)
                .collect(Collectors.toList());
    }

    @Override
    public long totalProductCount() {
        return productRepository.count();
    }

    @Override
    public ProductDto productInfo(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for: " + productCode));
        return productMapper.mapToProductDto(product);
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest, HttpServletRequest request) {
        tokenInterface.validateRole(request);

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

    @Override
    public Page<ProductDto> getFilteredProducts(BigDecimal minPrice, BigDecimal maxPrice, Category category, String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Product> spec = ProductSpecification.filterBy(minPrice, maxPrice, category, brand);
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productMapper::mapToProductDto);
    }


}
