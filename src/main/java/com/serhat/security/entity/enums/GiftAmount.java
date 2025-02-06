package com.serhat.security.entity.enums;

import lombok.Getter;

@Getter
public enum GiftAmount {
    HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    THOUSAND(1000);

    private final int amount;

    GiftAmount(int amount) {
        this.amount = amount;
    }


}
