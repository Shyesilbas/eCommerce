package com.serhat.security.service.user;

import com.serhat.security.dto.request.UpdateEmailRequest;
import com.serhat.security.dto.request.UpdateMembershipRequest;
import com.serhat.security.dto.request.UpdatePasswordRequest;
import com.serhat.security.dto.request.UpdatePhoneRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
public interface UserInterface {

    UserResponse userInfo(HttpServletRequest request);

    UpdatePhoneResponse updatePhone(HttpServletRequest request, UpdatePhoneRequest updatePhoneRequest);
    UpdateEmailResponse updateEmail(HttpServletRequest request, UpdateEmailRequest updateEmailRequest);

    UpdateMembershipPlan updateMembershipPlan(HttpServletRequest servletRequest, UpdateMembershipRequest request);

    default void updateUserTotalFees(User user) {
        BigDecimal totalShippingFee = user.getOrders().stream()
                .map(Order::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOrderFee = user.getOrders().stream()
                .map(order -> order.getTotalPaid().subtract(order.getShippingFee()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaved = user.getOrders().stream()
                .map(Order::getTotalSaved)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        user.setTotalShippingFeePaid(totalShippingFee);
        user.setTotalOrderFeePaid(totalOrderFee);
        user.setTotalSaved(totalSaved);
    }

}
