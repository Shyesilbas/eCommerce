package com.serhat.ecommerce.discount.dto.object;

import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.enums.GiftAmount;

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
