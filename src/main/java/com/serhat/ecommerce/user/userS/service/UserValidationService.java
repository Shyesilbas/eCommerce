package com.serhat.ecommerce.user.userS.service;

import com.serhat.ecommerce.auth.dto.request.RegisterRequest;

public interface UserValidationService {
    void validateUserRegistration(RegisterRequest request);
}
