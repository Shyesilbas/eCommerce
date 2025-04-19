package com.serhat.ecommerce.discount.dto.response;

import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.enums.GiftAmount;

import java.time.LocalDateTime;

public record GiftCardResponse(
        GiftAmount giftAmount,
        LocalDateTime expirationDate,
        CouponStatus status
) {
}
