package com.serhat.security.service;

import com.serhat.security.dto.response.PriceHistoryResponse;
import com.serhat.security.entity.PriceHistory;
import com.serhat.security.entity.Product;
import com.serhat.security.mapper.PriceHistoryMapper;
import com.serhat.security.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceHistoryService {
    private final PriceHistoryRepository priceHistoryRepository;
    private final PriceHistoryMapper mapper;

    public List<PriceHistoryResponse> getPriceHistory(Long productId) {
        List<PriceHistory> priceHistories = priceHistoryRepository.findByProduct_ProductIdOrderByChangeDateDesc(productId);

        return priceHistories.stream()
                .map(mapper::mapToPriceHistoryResponse)
                .toList();
    }

    public List<PriceHistoryResponse> getPriceHistoryInDateRange(Long productId, String startDate, String endDate) {
        LocalDateTime start = parseStartDate(startDate);
        LocalDateTime end = parseEndDate(endDate);

        List<PriceHistory> priceHistories = priceHistoryRepository.findByProduct_ProductIdAndChangeDateBetween(
                productId, start, end);

        return priceHistories.stream()
                .map(mapper::mapToPriceHistoryResponse)
                .toList();
    }

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


    private LocalDateTime parseStartDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter).atStartOfDay();
    }

    private LocalDateTime parseEndDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter).atTime(23, 59, 59, 999999);
    }



}
