package com.serhat.ecommerce.discount.dto.request;

import com.serhat.ecommerce.discount.enums.GiftAmount;

public record GenerateGiftCardRequest(
        GiftAmount amount
) {
}
