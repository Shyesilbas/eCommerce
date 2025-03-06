package com.serhat.security.service.password;

import com.serhat.security.dto.request.ForgotPasswordRequest;
import com.serhat.security.dto.request.UpdatePasswordRequest;
import com.serhat.security.dto.response.ForgotPasswordResponse;
import com.serhat.security.dto.response.UpdatePasswordResponse;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.exception.InvalidCredentialsException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final TokenInterface tokenInterface;

    @Override
    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("Invalid password attempt");
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    @Transactional
    @Override
    @CachePut(value = "userInfoCache", key = "#request.userPrincipal.name")
    public UpdatePasswordResponse updatePassword(HttpServletRequest request, UpdatePasswordRequest updatePasswordRequest) {
        User user = tokenInterface.getUserFromToken(request);

        if (user.getPassword().equals(updatePasswordRequest.newPassword())) {
            throw new RuntimeException("Passwords are same.");
        }
        if (!passwordEncoder.matches(updatePasswordRequest.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect.");
        }

        notificationService.addNotification(request, NotificationTopic.PASSWORD_UPDATE);
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
