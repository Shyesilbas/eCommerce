package com.serhat.ecommerce.dto.object;

import com.serhat.ecommerce.enums.CouponStatus;
import com.serhat.ecommerce.enums.GiftAmount;

import java.time.LocalDateTime;

public record GiftCardDto(
        Long id ,
        String code,
        GiftAmount giftAmount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        CouponStatus status
) {
}
