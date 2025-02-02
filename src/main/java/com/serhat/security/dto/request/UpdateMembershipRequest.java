package com.serhat.security.dto.request;

import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.entity.enums.PaymentMethod;

public record UpdateMembershipRequest(
        PaymentMethod paymentMethod,
        MembershipPlan membershipPlan
) {
}
