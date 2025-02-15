package com.serhat.security.interfaces;

import com.serhat.security.entity.GiftCard;

import java.math.BigDecimal;

public interface GiftCardValidationInterface {
    void validateGiftCardStatusAndAmount(GiftCard giftCard);
    GiftCard findGiftCardById(Long id);
    void compareGiftCardAmountWithOrderPrice(GiftCard giftCard, BigDecimal totalPrice);
}
