package com.serhat.ecommerce.user.userS.service;

import com.serhat.ecommerce.dto.request.UpdateEmailRequest;
import com.serhat.ecommerce.dto.request.UpdateMembershipRequest;
import com.serhat.ecommerce.dto.request.UpdatePhoneRequest;
import com.serhat.ecommerce.dto.response.*;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.user.userS.entity.User;

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