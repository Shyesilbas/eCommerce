package com.serhat.ecommerce.enums;

import lombok.Getter;

@Getter
public enum DiscountRate {
    TEN_PERCENT(10),
    TWENTY_PERCENT(20),
    THIRTY_PERCENT(30),
    ZERO(0);

    private final int percentage;

    DiscountRate(int percentage) {
        this.percentage = percentage;
    }

}
