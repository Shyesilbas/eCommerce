package com.serhat.ecommerce.auth.password;

import com.serhat.ecommerce.auth.dto.request.ForgotPasswordRequest;
import com.serhat.ecommerce.auth.dto.request.UpdatePasswordRequest;
import com.serhat.ecommerce.auth.dto.response.ForgotPasswordResponse;
import com.serhat.ecommerce.auth.dto.response.UpdatePasswordResponse;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.notification.enums.NotificationTopic;
import com.serhat.ecommerce.auth.authException.InvalidCredentialsException;
import com.serhat.ecommerce.user.userS.repository.UserRepository;
import com.serhat.ecommerce.notification.service.NotificationService;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService{

    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("Invalid password attempt");
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    @Transactional
    @Override
    @CachePut(value = "userInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public UpdatePasswordResponse updatePassword( UpdatePasswordRequest updatePasswordRequest) {
        User user = userService.getAuthenticatedUser();

        if (user.getPassword().equals(updatePasswordRequest.newPassword())) {
            throw new RuntimeException("Passwords are same.");
        }
        if (!passwordEncoder.matches(updatePasswordRequest.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect.");
        }

        notificationService.addNotification( user ,NotificationTopic.PASSWORD_UPDATE);
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.newPassword()));
        userRepository.save(user);
        return new UpdatePasswordResponse("Password updated successfully.", LocalDateTime.now());
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.email()));

        if (user.getPassword().equals(request.newPassword())) {
            throw new RuntimeException("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new ForgotPasswordResponse("Password updated successfully.", LocalDateTime.now());
    }
}
