package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record UserResponse(
        Long userId,
        String email,
        String username,
        String phone,
        Role role,
        int totalOrders,
        int cancelledOrders,
        BigDecimal bonusPoints,
        MembershipPlan membershipPlan,
        BigDecimal totalOrderFeePaid,
        BigDecimal totalShippingFeePaid,
        BigDecimal totalSaved
) implements Serializable {
}
