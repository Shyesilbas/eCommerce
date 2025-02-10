package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.entity.GiftCard;

import java.math.BigDecimal;

public interface GiftCardInterface {
    GiftCard applyGiftCard(OrderRequest orderRequest, BigDecimal totalPrice);
    void validateGiftCard(GiftCard giftCard);
}
