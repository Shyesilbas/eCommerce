package com.serhat.security.component.mapper;

import com.serhat.security.dto.response.PriceHistoryResponse;
import com.serhat.security.entity.PriceHistory;
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
