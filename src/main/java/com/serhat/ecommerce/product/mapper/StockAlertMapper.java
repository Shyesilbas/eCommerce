package com.serhat.ecommerce.product.mapper;

import com.serhat.ecommerce.product.dto.StockAlertResponse;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.product.enums.StockAlertType;
import com.serhat.ecommerce.product.entity.StockAlert;
import org.springframework.stereotype.Component;

@Component
public class StockAlertMapper {

    public StockAlert toStockAlert(Product product, StockAlertType alertType, String notes) {
        return StockAlert.builder()
                .product(product)
                .currentQuantity(product.getQuantity())
                .alertType(alertType)
                .notes(notes)
                .build();
    }

    public StockAlertResponse toStockAlertResponse(StockAlert stockAlert) {
        Product product = stockAlert.getProduct();
        return StockAlertResponse.builder()
                .message("Stock alert triggered")
                .productName(product.getName())
                .productCode(product.getProductCode())
                .quantity(product.getQuantity())
                .stockAlertType(stockAlert.getAlertType())
                .build();
    }
}