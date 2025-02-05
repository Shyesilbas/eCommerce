package com.serhat.security.dto.request;

import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateMembershipRequest(
        @NotNull
        MembershipPlan membershipPlan
) {
}
