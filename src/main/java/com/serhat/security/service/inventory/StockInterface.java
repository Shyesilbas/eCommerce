package com.serhat.security.service.inventory;

import com.serhat.security.entity.Product;

public interface StockInterface {
    void validateAndUpdateProductStock(Product product, int requestedQuantity);
}
