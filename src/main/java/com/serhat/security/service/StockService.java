package com.serhat.security.service;

import com.serhat.security.entity.Product;
import com.serhat.security.exception.InsufficientStockException;
import com.serhat.security.interfaces.ProductInterface;
import com.serhat.security.interfaces.StockInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService implements StockInterface {
    private final ProductInterface productInterface;
    private final StockAlertService stockAlertService;

    @Override
    public void validateAndUpdateProductStock(Product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        productInterface.updateProductStock(product, requestedQuantity);
        stockAlertService.handleStockAlert(product.getProductId());
    }
}
