package com.serhat.ecommerce.product.service;

import com.serhat.ecommerce.product.dto.PriceChangeInfo;
import com.serhat.ecommerce.product.dto.PriceHistoryResponse;
import com.serhat.ecommerce.product.entity.PriceHistory;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.product.mapper.PriceHistoryMapper;
import com.serhat.ecommerce.product.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceHistoryServiceImpl implements PriceHistoryService{
    private final PriceHistoryRepository priceHistoryRepository;
    private final PriceHistoryMapper mapper;

    @Override
    public Page<PriceHistoryResponse> getPriceHistory(Long productId, Pageable pageable) {
        Page<PriceHistory> priceHistoryPage = priceHistoryRepository
                .findByProduct_ProductIdOrderByChangeDateDesc(productId, pageable);

        return priceHistoryPage.map(mapper::mapToPriceHistoryResponse);
    }

    @Override
    public Page<PriceHistoryResponse> getPriceHistoryInDateRange(Long productId, String startDate, String endDate, Pageable pageable) {
        LocalDateTime start = parseStartDate(startDate);
        LocalDateTime end = parseEndDate(endDate);

        Page<PriceHistory> priceHistoryPage = priceHistoryRepository.findByProduct_ProductIdAndChangeDateBetween(
                productId, start, end, pageable);

        return priceHistoryPage.map(mapper::mapToPriceHistoryResponse);
    }

    @Override
    public void createAndSavePriceHistory(Product product, BigDecimal oldPrice, BigDecimal newPrice, double changePercentage, double totalChangePercentage) {
        PriceHistory priceHistory = PriceHistory.builder()
                .product(product)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changePercentage(changePercentage)
                .totalChangePercentage(totalChangePercentage)
                .changeDate(LocalDateTime.now())
                .build();

        priceHistoryRepository.save(priceHistory);
        log.info("Price history saved for product: {}", product.getProductId());
    }

    @Override
    public double calculateTotalChangePercentage(Long productId, BigDecimal currentPrice) {
        PriceHistory firstPriceHistory = priceHistoryRepository.findFirstByProduct_ProductIdOrderByChangeDateAsc(productId);
        BigDecimal firstPrice = firstPriceHistory == null ? currentPrice : firstPriceHistory.getOldPrice();
        return calculateChangePercentage(firstPrice, currentPrice);
    }

    @Override
    public double calculateChangePercentage(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return newPrice.subtract(oldPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Override
    public PriceChangeInfo calculatePriceChanges(Long productId, BigDecimal oldPrice, BigDecimal newPrice) {
        double changePercentage = calculateChangePercentage(oldPrice, newPrice);
        double totalChangePercentage = calculateTotalChangePercentage(productId, newPrice);
        return new PriceChangeInfo(changePercentage, totalChangePercentage);
    }


    private LocalDateTime parseStartDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter).atStartOfDay();
    }

    private LocalDateTime parseEndDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter).atTime(23, 59, 59, 999999);
    }



}
