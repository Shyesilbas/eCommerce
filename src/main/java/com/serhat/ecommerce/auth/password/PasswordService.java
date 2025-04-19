package com.serhat.ecommerce.auth.password;

import com.serhat.ecommerce.auth.dto.request.ForgotPasswordRequest;
import com.serhat.ecommerce.auth.dto.request.UpdatePasswordRequest;
import com.serhat.ecommerce.auth.dto.response.ForgotPasswordResponse;
import com.serhat.ecommerce.auth.dto.response.UpdatePasswordResponse;

public interface PasswordService {
    void validatePassword(String rawPassword, String encodedPassword);
    UpdatePasswordResponse updatePassword( UpdatePasswordRequest updatePasswordRequest);
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);
}
