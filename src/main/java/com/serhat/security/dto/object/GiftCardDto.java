package com.serhat.security.dto.object;

import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;

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
