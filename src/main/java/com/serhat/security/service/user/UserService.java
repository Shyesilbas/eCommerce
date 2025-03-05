package com.serhat.security.service.user;

import com.serhat.security.dto.request.*;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.interfaces.UserInterface;
import com.serhat.security.mapper.UserMapper;
import com.serhat.security.repository.UserRepository;
import com.serhat.security.service.NotificationService;
import com.serhat.security.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserInterface {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final UserMapper userMapper;
    private final TokenInterface tokenInterface;

    public User getUserFromToken(HttpServletRequest request){
        return tokenInterface.getUserFromToken(request);
    }

    @CachePut(value = "userInfoCache", key = "#servletRequest.userPrincipal.name")
    @Transactional
    @Override
    public UpdateMembershipPlan updateMembershipPlan(HttpServletRequest servletRequest, UpdateMembershipRequest request) {
        User user = getUserFromToken(servletRequest);
        if (user.getMembershipPlan().equals(request.membershipPlan())) {
            throw new SamePlanRequestException("You requested the same plan that you currently have.");
        }

        BigDecimal fee = request.membershipPlan().getFee();
        transactionService.createMembershipTransaction(user, fee);
        user.setMembershipPlan(request.membershipPlan());
        log.info("Membership plan updated for {} with payment method: {}", user.getUsername(), PaymentMethod.E_WALLET);

        return new UpdateMembershipPlan(request.membershipPlan(), fee, "Membership plan updated successfully.");
    }

    @CachePut(value = "userInfoCache", key = "#request.userPrincipal.name")
    @Transactional
    @Override
    public UpdatePhoneResponse updatePhone(HttpServletRequest request, UpdatePhoneRequest updatePhoneRequest) {
        User user = getUserFromToken(request);

        if (userRepository.findByPhone(updatePhoneRequest.phone()).isPresent()) {
            throw new EmailAlreadyExistException("Phone already exists!");
        }

        notificationService.addNotification(request, NotificationTopic.PHONE_UPDATE);
        user.setPhone(updatePhoneRequest.phone());
        userRepository.save(user);

        return new UpdatePhoneResponse("Phone updated successfully.", user.getPhone(), LocalDateTime.now());
    }

    @CachePut(value = "userInfoCache", key = "#request.userPrincipal.name")
    @Transactional
    @Override
    public UpdateEmailResponse updateEmail(HttpServletRequest request, UpdateEmailRequest updateEmailRequest) {
        User user = getUserFromToken(request);

        if (userRepository.findByEmail(updateEmailRequest.newEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exists!");
        }

        notificationService.addNotification(request, NotificationTopic.EMAIL_UPDATE);
        user.setEmail(updateEmailRequest.newEmail());
        userRepository.save(user);

        return new UpdateEmailResponse("Email updated successfully.", user.getEmail(), LocalDateTime.now());
    }

    @Transactional
    @Override
    @CachePut(value = "userInfoCache", key = "#request.userPrincipal.name")
    public UpdatePasswordResponse updatePassword(HttpServletRequest request, UpdatePasswordRequest updatePasswordRequest) {
        User user = getUserFromToken(request);

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

    @Cacheable(value = "userInfoCache", key = "#request.userPrincipal.name", unless = "#result == null")
    @Override
    public UserResponse userInfo(HttpServletRequest request) {
        User user = getUserFromToken(request);
        UserResponse response = userMapper.toUserResponse(user);

        log.info("User details fetched from DATABASE: userId={}, email={}, username={}, role={}, total orders={}",
                user.getUserId(), user.getEmail(), user.getUsername(), user.getRole(), user.getTotalOrders());

        return response;
    }

}
