package com.serhat.ecommerce.dto.request;

import com.serhat.ecommerce.enums.MembershipPlan;
import jakarta.validation.constraints.NotNull;

public record UpdateMembershipRequest(
        @NotNull
        MembershipPlan membershipPlan
) {
}
