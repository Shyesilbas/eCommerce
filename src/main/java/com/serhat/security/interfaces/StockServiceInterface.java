package com.serhat.security.interfaces;

import com.serhat.security.entity.Product;

public interface StockServiceInterface {
    void validateProductStock(Product product, int requestedQuantity);
    void updateProductStock(Product product, int quantity);
    void manageStockAlerts(Long productId);
}

