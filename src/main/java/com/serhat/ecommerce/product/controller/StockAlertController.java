package com.serhat.ecommerce.product.controller;

import com.serhat.ecommerce.dto.response.StockAlertResponse;
import com.serhat.ecommerce.enums.StockAlertType;
import com.serhat.ecommerce.product.inventory.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stockAlerts")
public class StockAlertController {
    private final StockAlertService stockAlertService;

    @GetMapping
    public ResponseEntity<List<StockAlertResponse>> getAllStockAlerts() {
        List<StockAlertResponse> stockAlerts = stockAlertService.getAllStockAlerts();
        return ResponseEntity.ok(stockAlerts);
    }

    @GetMapping("/byType")
    public ResponseEntity<List<StockAlertResponse>> getStockAlertsByType(@RequestParam StockAlertType alertType) {
        List<StockAlertResponse> stockAlerts = stockAlertService.getStockAlertsByType(alertType);
        return ResponseEntity.ok(stockAlerts);
    }
}
