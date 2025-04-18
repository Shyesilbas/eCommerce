package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.CouponStatus;
import com.serhat.ecommerce.enums.GiftAmount;

import java.time.LocalDateTime;

public record GiftCardResponse(
        GiftAmount giftAmount,
        LocalDateTime expirationDate,
        CouponStatus status
) {
}
