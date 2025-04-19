package com.serhat.ecommerce.user.userS.service;

import com.serhat.ecommerce.user.userS.dto.UpdateEmailRequest;
import com.serhat.ecommerce.user.userS.dto.UpdateMembershipRequest;
import com.serhat.ecommerce.user.userS.dto.UpdatePhoneRequest;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.user.userS.dto.UpdateEmailResponse;
import com.serhat.ecommerce.user.userS.dto.UpdateMembershipPlan;
import com.serhat.ecommerce.user.userS.dto.UpdatePhoneResponse;
import com.serhat.ecommerce.user.userS.dto.UserResponse;
import com.serhat.ecommerce.user.userS.entity.User;

import java.math.BigDecimal;

public interface UserService {
    UserResponse userInfo();
    UpdatePhoneResponse updatePhone(UpdatePhoneRequest updatePhoneRequest);
    UpdateEmailResponse updateEmail(UpdateEmailRequest updateEmailRequest);
    UpdateMembershipPlan updateMembershipPlan(UpdateMembershipRequest request);
    void updateUserAfterOrderCancel(User user, Order order);
    void updateUserAfterOrder(Order order, User user);
    User getAuthenticatedUser();
    void updateUserBonusPoints(User user, BigDecimal bonusPoints);
    void saveUser(User user);
}