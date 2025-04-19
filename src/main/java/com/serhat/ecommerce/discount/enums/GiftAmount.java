package com.serhat.ecommerce.discount.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum GiftAmount {
    HUNDRED(new BigDecimal("100.00")),
    TWO_HUNDRED(new BigDecimal("200.00")),
    FIVE_HUNDRED(new BigDecimal("500.00")),
    THOUSAND(new BigDecimal("1000.00"));

    private final BigDecimal amount;

    GiftAmount(BigDecimal amount) {
        this.amount = amount;
    }


}
