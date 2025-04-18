package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.MembershipPlan;

import java.math.BigDecimal;

public record UpdateMembershipPlan(
        MembershipPlan newPlan,
        BigDecimal fee,
        String paymentMessage
) {
}
