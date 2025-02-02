package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.MembershipPlan;

import java.math.BigDecimal;

public record UpdateMembershipPlan(
        MembershipPlan newPlan,
        BigDecimal fee,
        String paymentMessage
) {
}
