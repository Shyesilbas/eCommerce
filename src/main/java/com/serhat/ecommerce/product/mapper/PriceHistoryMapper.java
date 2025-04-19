package com.serhat.ecommerce.product.mapper;

import com.serhat.ecommerce.product.dto.PriceHistoryResponse;
import com.serhat.ecommerce.product.entity.PriceHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceHistoryMapper {
    public PriceHistoryResponse mapToPriceHistoryResponse(PriceHistory priceHistory) {
        return new PriceHistoryResponse(
                priceHistory.getProduct().getName(),
                priceHistory.getProduct().getProductId(),
                priceHistory.getOldPrice(),
                priceHistory.getNewPrice(),
                priceHistory.getChangePercentage(),
                priceHistory.getTotalChangePercentage(),
                priceHistory.getChangeDate()
        );
    }
}
