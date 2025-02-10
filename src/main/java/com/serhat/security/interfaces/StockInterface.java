package com.serhat.security.interfaces;

import com.serhat.security.entity.Product;

public interface StockInterface {
    void validateAndUpdateProductStock(Product product, int requestedQuantity);
}
