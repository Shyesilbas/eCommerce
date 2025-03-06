package com.serhat.security.service.auth.password;

import com.serhat.security.dto.request.ForgotPasswordRequest;
import com.serhat.security.dto.request.UpdatePasswordRequest;
import com.serhat.security.dto.response.ForgotPasswordResponse;
import com.serhat.security.dto.response.UpdatePasswordResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PasswordService {
    void validatePassword(String rawPassword, String encodedPassword);
    UpdatePasswordResponse updatePassword(HttpServletRequest request, UpdatePasswordRequest updatePasswordRequest);
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);
}
