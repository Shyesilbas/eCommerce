package com.serhat.ecommerce.dto.request;

import com.serhat.ecommerce.enums.GiftAmount;

public record GenerateGiftCardRequest(
        GiftAmount amount
) {
}
