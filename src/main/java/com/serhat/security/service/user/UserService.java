package com.serhat.security.service.user;

import com.serhat.security.dto.request.UpdateEmailRequest;
import com.serhat.security.dto.request.UpdateMembershipRequest;
import com.serhat.security.dto.request.UpdatePhoneRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;

import java.math.BigDecimal;

public interface UserService {
    UserResponse userInfo();
    UpdatePhoneResponse updatePhone( UpdatePhoneRequest updatePhoneRequest);
    UpdateEmailResponse updateEmail( UpdateEmailRequest updateEmailRequest);
    UpdateMembershipPlan updateMembershipPlan(UpdateMembershipRequest request);
    void updateUserAfterOrderCancel(User user, Order order);
    void updateUserAfterOrder(Order order, User user);
    User getAuthenticatedUser();
    void updateUserBonusPoints(User user, BigDecimal bonusPoints);
    void saveUser(User user);
}