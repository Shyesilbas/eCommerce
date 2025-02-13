package com.serhat.security.interfaces;

import com.serhat.security.dto.request.RegisterRequest;

public interface UserValidationInterface {
    void validateUserRegistration(RegisterRequest request);
}
