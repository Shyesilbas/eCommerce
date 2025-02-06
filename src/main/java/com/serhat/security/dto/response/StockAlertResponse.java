package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.StockAlertType;
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
