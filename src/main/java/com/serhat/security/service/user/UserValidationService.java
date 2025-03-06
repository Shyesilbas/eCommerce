package com.serhat.security.service.user;

import com.serhat.security.dto.request.RegisterRequest;

public interface UserValidationService {
    void validateUserRegistration(RegisterRequest request);
}
