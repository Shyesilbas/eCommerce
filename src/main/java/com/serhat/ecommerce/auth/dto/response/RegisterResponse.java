package com.serhat.ecommerce.auth.dto.response;

import com.serhat.ecommerce.user.enums.MembershipPlan;

import java.time.LocalDateTime;

public record RegisterResponse(
        String message,
        String name,
        String email,
        MembershipPlan membershipPlan,
        LocalDateTime time
) {
}
