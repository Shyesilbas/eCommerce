package com.serhat.security.controller;

import com.serhat.security.dto.response.PriceHistoryResponse;
import com.serhat.security.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-history")
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    @GetMapping("/of-product")
    public ResponseEntity<List<PriceHistoryResponse>> priceHistory(@RequestParam Long productId){
        return ResponseEntity.ok(priceHistoryService.getPriceHistory(productId));
    }
}
