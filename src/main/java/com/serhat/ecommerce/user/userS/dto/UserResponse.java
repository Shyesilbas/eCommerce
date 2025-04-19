package com.serhat.ecommerce.user.userS.dto;

import com.serhat.ecommerce.user.enums.MembershipPlan;
import com.serhat.ecommerce.user.enums.Role;
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
