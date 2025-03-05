package com.serhat.security.service.password;

public interface PasswordService {
    void validatePassword(String rawPassword, String encodedPassword);
}
