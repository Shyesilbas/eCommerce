package com.serhat.security.component.mapper;

import com.serhat.security.dto.response.StockAlertResponse;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.StockAlert;
import com.serhat.security.entity.enums.StockAlertType;
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