package com.serhat.ecommerce.user.userS.service;

import com.serhat.ecommerce.auth.dto.RegisterRequest;
import com.serhat.ecommerce.user.userException.EmailAlreadyExistException;
import com.serhat.ecommerce.user.userException.PhoneAlreadyExistsException;
import com.serhat.ecommerce.user.userException.UsernameAlreadyExists;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userS.repository.UserRepository;
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
