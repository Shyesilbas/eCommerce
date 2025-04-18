package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.MembershipPlan;

import java.time.LocalDateTime;

public record RegisterResponse(
        String message,
        String name,
        String email,
        MembershipPlan membershipPlan,
        LocalDateTime time
) {
}
