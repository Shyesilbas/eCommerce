package com.serhat.security.interfaces;

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

    User getUserFromToken(HttpServletRequest request);
    UserResponse userInfo(HttpServletRequest request);

    UpdatePhoneResponse updatePhone(HttpServletRequest request, UpdatePhoneRequest updatePhoneRequest);
    UpdateEmailResponse updateEmail(HttpServletRequest request, UpdateEmailRequest updateEmailRequest);
    UpdatePasswordResponse updatePassword(HttpServletRequest request, UpdatePasswordRequest updatePasswordRequest);

    UpdateMembershipPlan updateMembershipPlan(HttpServletRequest servletRequest, UpdateMembershipRequest request);

}
