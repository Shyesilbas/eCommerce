package com.serhat.ecommerce.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum MembershipPlan {
    BASIC(BigDecimal.ZERO),
    PREMIUM(new BigDecimal("9.99")),
    VIP(new BigDecimal("14.99"));

    private final BigDecimal fee;

    MembershipPlan(BigDecimal fee) {
        this.fee = fee;
    }

}
