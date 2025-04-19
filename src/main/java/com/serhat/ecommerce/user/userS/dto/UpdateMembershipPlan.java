package com.serhat.ecommerce.user.userS.dto;

import com.serhat.ecommerce.user.enums.MembershipPlan;

import java.math.BigDecimal;

public record UpdateMembershipPlan(
        MembershipPlan newPlan,
        BigDecimal fee,
        String paymentMessage
) {
}
