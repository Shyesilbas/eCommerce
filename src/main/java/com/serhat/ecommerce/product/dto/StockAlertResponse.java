package com.serhat.ecommerce.product.dto;

import com.serhat.ecommerce.product.enums.StockAlertType;
import lombok.Builder;

@Builder
public record StockAlertResponse(
        String message,
        String productName,
        String productCode,
        int quantity,
        StockAlertType stockAlertType
) {
}
