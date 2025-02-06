package com.serhat.security.dto.request;

import com.serhat.security.entity.enums.GiftAmount;

public record GenerateGiftCardRequest(
        GiftAmount amount
) {
}
