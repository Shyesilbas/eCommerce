package com.serhat.security.service;

import com.serhat.security.dto.response.StockAlertResponse;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.StockAlert;
import com.serhat.security.entity.enums.StockAlertType;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.mapper.StockAlertMapper;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockAlertService {
    private final ProductRepository productRepository;
    private final StockAlertMapper stockAlertMapper;
    private final StockAlertRepository stockAlertRepository;

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
    }

    @Transactional
    public void handleStockAlert(Long productId) {
        Product product = findProductById(productId);
        productRepository.save(product);
        product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

        StockAlert existingAlert = stockAlertRepository.findByProduct(product);

        if (product.getQuantity() == 0) {
            if (existingAlert != null && existingAlert.getAlertType() == StockAlertType.LOW_STOCK) {
                stockAlertRepository.delete(existingAlert);
                log.info("Deleted low stock alert for product: {}", product.getName());
            }

            if (existingAlert == null || existingAlert.getAlertType() != StockAlertType.OUT_OF_STOCK) {
                String notes = "Product is out of stock alert for product: " + product.getName();
                StockAlert stockAlert = stockAlertMapper.toStockAlert(product, StockAlertType.OUT_OF_STOCK, notes);
                stockAlertRepository.save(stockAlert);
                log.info("Created out of stock alert for product: {}", product.getName());
            } else if (existingAlert != null && existingAlert.getAlertType() == StockAlertType.OUT_OF_STOCK) {
                existingAlert.setCurrentQuantity(product.getQuantity());
                stockAlertRepository.save(existingAlert);
                log.info("Updated out of stock alert for product: {} with new quantity: {}", product.getName(), product.getQuantity());
            }
        } else if (product.getQuantity() <= 10) {
            if (existingAlert != null && existingAlert.getAlertType() == StockAlertType.OUT_OF_STOCK) {
                stockAlertRepository.delete(existingAlert);
                log.info("Deleted out of stock alert for product: {}", product.getName());
            }

            if (existingAlert == null || existingAlert.getAlertType() != StockAlertType.LOW_STOCK) {
                String notes = "Low stock alert for product: " + product.getName();
                StockAlert stockAlert = stockAlertMapper.toStockAlert(product, StockAlertType.LOW_STOCK, notes);
                stockAlertRepository.save(stockAlert);
                log.info("Created low stock alert for product: {}", product.getName());
            } else if (existingAlert != null && existingAlert.getAlertType() == StockAlertType.LOW_STOCK) {
                existingAlert.setCurrentQuantity(product.getQuantity());
                stockAlertRepository.save(existingAlert);
                log.info("Updated low stock alert for product: {} with new quantity: {}", product.getName(), product.getQuantity());
            }
        } else if (product.getQuantity() > 10) {
            if (existingAlert != null && existingAlert.getAlertType() == StockAlertType.LOW_STOCK) {
                stockAlertRepository.delete(existingAlert);
                log.info("Deleted low stock alert for product: {}", product.getName());
            }
        }
    }



    public List<StockAlertResponse> getAllStockAlerts() {
        List<StockAlert> stockAlerts = stockAlertRepository.findAll();
        return stockAlerts.stream()
                .map(stockAlertMapper::toStockAlertResponse)
                .collect(Collectors.toList());
    }

    public List<StockAlertResponse> getStockAlertsByType(StockAlertType alertType) {
        List<StockAlert> stockAlerts = stockAlertRepository.findByAlertType(alertType);
        return stockAlerts.stream()
                .map(stockAlertMapper::toStockAlertResponse)
                .collect(Collectors.toList());
    }


}