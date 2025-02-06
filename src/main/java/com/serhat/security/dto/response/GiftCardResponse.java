package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GiftCardResponse(
        GiftAmount giftAmount,
        LocalDateTime expirationDate,
        CouponStatus status
) {
}
