package com.serhat.security.service;

import com.serhat.security.dto.response.PriceHistoryResponse;
import com.serhat.security.entity.PriceHistory;
import com.serhat.security.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceHistoryService {
    private final PriceHistoryRepository priceHistoryRepository;

    public List<PriceHistoryResponse> getPriceHistory(Long productId) {
        List<PriceHistory> priceHistories = priceHistoryRepository.findByProduct_ProductIdOrderByChangeDateDesc(productId);

        return priceHistories.stream()
                .map(priceHistory -> new PriceHistoryResponse(
                        priceHistory.getProduct().getName(),
                        priceHistory.getProduct().getProductId(),
                        priceHistory.getOldPrice(),
                        priceHistory.getNewPrice(),
                        priceHistory.getChangePercentage(),
                        priceHistory.getTotalChangePercentage(),
                        priceHistory.getChangeDate()
                ))
                .toList();
    }

    public List<PriceHistoryResponse> getPriceHistoryInDateRange(Long productId, String startDate, String endDate) {
        LocalDateTime start = parseStartDate(startDate);
        LocalDateTime end = parseEndDate(endDate);

        List<PriceHistory> priceHistories = priceHistoryRepository.findByProduct_ProductIdAndChangeDateBetween(
                productId, start, end);

        return priceHistories.stream()
                .map(priceHistory -> new PriceHistoryResponse(
                        priceHistory.getProduct().getName(),
                        priceHistory.getProduct().getProductId(),
                        priceHistory.getOldPrice(),
                        priceHistory.getNewPrice(),
                        priceHistory.getChangePercentage(),
                        priceHistory.getTotalChangePercentage(),
                        priceHistory.getChangeDate()
                ))
                .toList();
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
