package com.serhat.security.service.inventory;

import com.serhat.security.entity.Product;
import com.serhat.security.exception.InsufficientStockException;
import com.serhat.security.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService implements StockInterface {
    private final ProductService productService;
    private final StockAlertService stockAlertService;

    @Override
    public void validateAndUpdateProductStock(Product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        productService.updateProductStock(product, requestedQuantity);
        stockAlertService.handleStockAlert(product.getProductId());
    }
}
