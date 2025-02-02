package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.MembershipPlan;

import java.time.LocalDateTime;

public record RegisterResponse(
        String message,
        String name,
        String email,
        MembershipPlan membershipPlan,
        LocalDateTime time
) {
}
