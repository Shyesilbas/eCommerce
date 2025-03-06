package com.serhat.security.service.auth.password;

import com.serhat.security.dto.request.ForgotPasswordRequest;
import com.serhat.security.dto.request.UpdatePasswordRequest;
import com.serhat.security.dto.response.ForgotPasswordResponse;
import com.serhat.security.dto.response.UpdatePasswordResponse;

public interface PasswordService {
    void validatePassword(String rawPassword, String encodedPassword);
    UpdatePasswordResponse updatePassword( UpdatePasswordRequest updatePasswordRequest);
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);
}
