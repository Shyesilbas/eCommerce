package com.serhat.security.interfaces;

import com.serhat.security.dto.request.RegisterRequest;

public interface UserValidationService {
    void validateUserRegistration(RegisterRequest request);
}
