package com.serhat.ecommerce.user.userS.dto;

import com.serhat.ecommerce.user.enums.MembershipPlan;
import jakarta.validation.constraints.NotNull;

public record UpdateMembershipRequest(
        @NotNull
        MembershipPlan membershipPlan
) {
}
