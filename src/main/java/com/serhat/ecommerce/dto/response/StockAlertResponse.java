package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.StockAlertType;
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
