package com.serhat.ecommerce.product.service;

import com.serhat.ecommerce.dto.response.PriceChangeInfo;
import com.serhat.ecommerce.dto.response.PriceHistoryResponse;
import com.serhat.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PriceHistoryService {
    Page<PriceHistoryResponse> getPriceHistory(Long productId, Pageable pageable);
    Page<PriceHistoryResponse> getPriceHistoryInDateRange(Long productId, String startDate, String endDate, Pageable pageable);
    void createAndSavePriceHistory(Product product, BigDecimal oldPrice, BigDecimal newPrice, double changePercentage, double totalChangePercentage);
    double calculateTotalChangePercentage(Long productId, BigDecimal currentPrice);
    double calculateChangePercentage(BigDecimal oldPrice, BigDecimal newPrice);
    PriceChangeInfo calculatePriceChanges(Long productId, BigDecimal oldPrice, BigDecimal newPrice);
}
