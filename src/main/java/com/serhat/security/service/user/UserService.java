package com.serhat.security.service.user;

import com.serhat.security.dto.request.UpdateEmailRequest;
import com.serhat.security.dto.request.UpdateMembershipRequest;
import com.serhat.security.dto.request.UpdatePhoneRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserResponse userInfo(HttpServletRequest request);
    UpdatePhoneResponse updatePhone(HttpServletRequest request, UpdatePhoneRequest updatePhoneRequest);
    UpdateEmailResponse updateEmail(HttpServletRequest request, UpdateEmailRequest updateEmailRequest);
    UpdateMembershipPlan updateMembershipPlan(HttpServletRequest servletRequest, UpdateMembershipRequest request);
    void updateUserTotalFees(User user);
    void updateUserAfterOrderCancel(User user, Order order);
    void updateUserAfterOrder(Order order, User user);
    User getAuthenticatedUser();
}