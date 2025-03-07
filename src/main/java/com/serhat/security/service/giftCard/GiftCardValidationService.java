package com.serhat.security.service.giftCard;

import com.serhat.security.entity.GiftCard;

import java.math.BigDecimal;

public interface GiftCardValidationService {
    void validateGiftCardStatusAndAmount(GiftCard giftCard);
    GiftCard findGiftCardById(Long id);
    void compareGiftCardAmountWithOrderPrice(GiftCard giftCard, BigDecimal totalPrice);
    void saveGiftCard(GiftCard giftCard);
}
