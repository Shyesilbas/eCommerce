package com.serhat.ecommerce.product.controller;

import com.serhat.ecommerce.product.dto.PriceHistoryResponse;
import com.serhat.ecommerce.product.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-history")
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    @GetMapping("/of-product")
    public ResponseEntity<Page<PriceHistoryResponse>> priceHistory(
            @RequestParam Long productId,
            Pageable pageable) {
        Page<PriceHistoryResponse> priceHistoryPage = priceHistoryService.getPriceHistory(productId, pageable);
        return ResponseEntity.ok(priceHistoryPage);
    }



    @GetMapping("/in-date-range")
    public ResponseEntity<Page<PriceHistoryResponse>> getPriceHistoryInDateRange(
            @RequestParam Long productId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Pageable pageable) {
        return ResponseEntity.ok(priceHistoryService.getPriceHistoryInDateRange(productId, startDate, endDate,pageable));
    }
}
