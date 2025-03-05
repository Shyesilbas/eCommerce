package com.serhat.security.service.user;

import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.entity.User;
import com.serhat.security.exception.EmailAlreadyExistException;
import com.serhat.security.exception.PhoneAlreadyExistsException;
import com.serhat.security.exception.UsernameAlreadyExists;
import com.serhat.security.interfaces.UserValidationService;
import com.serhat.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {
    private final UserRepository userRepository;

    @Override
    public void validateUserRegistration(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmailOrUsernameOrPhone(
                request.email(),
                request.username(),
                request.phone()
        );

        existingUser.ifPresent(user -> {
            if (user.getEmail().equals(request.email())) {
                throw new EmailAlreadyExistException("Email already exists!");
            }
            if (user.getUsername().equals(request.username())) {
                throw new UsernameAlreadyExists("Username already exists!");
            }
            if (user.getPhone().equals(request.phone())) {
                throw new PhoneAlreadyExistsException("Phone number already exists!");
            }
        });
    }

}
