package com.serhat.ecommerce.user.userS.mapper;

import com.serhat.ecommerce.auth.dto.request.RegisterRequest;
import com.serhat.ecommerce.user.address.mapper.AddressMapper;
import com.serhat.ecommerce.discount.dto.response.AddBonusResponse;
import com.serhat.ecommerce.user.userS.dto.UserResponse;
import com.serhat.ecommerce.user.address.entity.Address;
import com.serhat.ecommerce.user.enums.MembershipPlan;
import com.serhat.ecommerce.user.userS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final AddressMapper addressMapper;
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
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

    public AddBonusResponse toAddBonusResponse(User user,BigDecimal amount){
        return new AddBonusResponse(
                amount.toString() + " bonus added to your bonus balance!",
                amount,
                user.getCurrentBonusPoints(),
                user.getBonusPointsWon()
        );
    }

    public User toUser(RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .email(request.email())
                .role(request.role())
                .bonusPointsWon(BigDecimal.ZERO)
                .currentBonusPoints(BigDecimal.ZERO)
                .totalOrders(0)
                .cancelledOrders(0)
                .membershipPlan(MembershipPlan.BASIC)
                .totalOrderFeePaid(BigDecimal.ZERO)
                .totalShippingFeePaid(BigDecimal.ZERO)
                .totalSaved(BigDecimal.ZERO)
                .build();

        if (request.address() != null && !request.address().isEmpty()) {
            List<Address> addresses = request.address().stream()
                    .map(addAddressRequest -> addressMapper.toAddress(addAddressRequest, user))
                    .collect(Collectors.toList());
            user.setAddresses(addresses);
        }
        return user;
    }

}
