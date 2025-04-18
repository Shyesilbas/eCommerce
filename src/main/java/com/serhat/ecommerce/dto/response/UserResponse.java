package com.serhat.ecommerce.dto.response;

import com.serhat.ecommerce.enums.MembershipPlan;
import com.serhat.ecommerce.enums.Role;
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
