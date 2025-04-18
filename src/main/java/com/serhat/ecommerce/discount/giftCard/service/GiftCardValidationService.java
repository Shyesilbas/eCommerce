package com.serhat.ecommerce.discount.giftCard.service;

import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;

import java.math.BigDecimal;

public interface GiftCardValidationService {
    void validateGiftCardStatusAndAmount(GiftCard giftCard);
    GiftCard findGiftCardById(Long id);
    void compareGiftCardAmountWithOrderPrice(GiftCard giftCard, BigDecimal totalPrice);
    void saveGiftCard(GiftCard giftCard);
}
