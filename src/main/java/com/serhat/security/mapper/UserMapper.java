package com.serhat.security.mapper;

import com.serhat.security.dto.response.UserResponse;
import com.serhat.security.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .password(user.getPassword())
                .totalOrders(user.getTotalOrders())
                .cancelledOrders(user.getCancelledOrders())
                .bonusPoints(user.getBonusPointsWon())
                .membershipPlan(user.getMembershipPlan())
                .role(user.getRole())
                .totalOrderFeePaid(user.getTotalOrderFeePaid())
                .totalShippingFeePaid(user.getTotalShippingFeePaid())
                .totalSaved(user.getTotalSaved())
                .build();

    }
}
