package com.serhat.ecommerce.user.userS.service;

import com.serhat.ecommerce.user.userException.EmailAlreadyExistException;
import com.serhat.ecommerce.user.userException.SamePlanRequestException;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.notification.enums.NotificationTopic;
import com.serhat.ecommerce.payment.enums.PaymentMethod;
import com.serhat.ecommerce.notification.service.NotificationService;
import com.serhat.ecommerce.payment.service.TransactionService;
import com.serhat.ecommerce.user.userS.dto.*;
import com.serhat.ecommerce.user.userS.mapper.UserMapper;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final UserMapper userMapper;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }

    @CachePut(value = "userInfoCache",key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    @Transactional
    @Override
    public UpdateMembershipPlan updateMembershipPlan(UpdateMembershipRequest request) {
        User user = getAuthenticatedUser();
        if (user.getMembershipPlan().equals(request.membershipPlan())) {
            throw new SamePlanRequestException("You requested the same plan that you currently have.");
        }

        BigDecimal fee = request.membershipPlan().getFee();
        transactionService.createMembershipTransaction(user, fee);
        user.setMembershipPlan(request.membershipPlan());
        userRepository.save(user);
        log.info("Membership plan updated for {} with payment method: {}", user.getUsername(), PaymentMethod.E_WALLET);

        return new UpdateMembershipPlan(request.membershipPlan(), fee, "Membership plan updated successfully.");
    }

    @CachePut(value = "userInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    @Transactional
    @Override
    public UpdatePhoneResponse updatePhone(UpdatePhoneRequest updatePhoneRequest) {
        User user = getAuthenticatedUser();
        validateUniquePhone(updatePhoneRequest.phone());
        user.setPhone(updatePhoneRequest.phone());
        userRepository.save(user);
        notificationService.addNotification(user, NotificationTopic.PHONE_UPDATE);
        return new UpdatePhoneResponse("Phone updated successfully.", user.getPhone(), LocalDateTime.now());
    }

    @CachePut(value = "userInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    @Transactional
    @Override
    public UpdateEmailResponse updateEmail(UpdateEmailRequest updateEmailRequest) {
        User user = getAuthenticatedUser();
        validateUniqueEmail(updateEmailRequest.newEmail());
        user.setEmail(updateEmailRequest.newEmail());
        userRepository.save(user);
        notificationService.addNotification(user, NotificationTopic.EMAIL_UPDATE);
        return new UpdateEmailResponse("Email updated successfully.", user.getEmail(), LocalDateTime.now());
    }

    @Cacheable(value = "userInfoCache",  key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()", unless = "#result == null")
    @Override
    public UserResponse userInfo() {
        User user = getAuthenticatedUser();
        UserResponse response = userMapper.toUserResponse(user);

        log.info("User details fetched: userId={}, email={}, username={}, role={}, total orders={}",
                user.getUserId(), user.getEmail(), user.getUsername(), user.getRole(), user.getTotalOrders());

        return response;
    }

    @Override
    public void updateUserAfterOrderCancel(User user, Order order) {
        user.setCancelledOrders(user.getCancelledOrders() + 1);
        user.setTotalShippingFeePaid(safeSubtract(user.getTotalShippingFeePaid(), order.getShippingFee()));
        user.setTotalOrderFeePaid(safeSubtract(user.getTotalOrderFeePaid(), order.getTotalPaid()));
        user.setBonusPointsWon(safeSubtract(user.getBonusPointsWon(), order.getBonusWon()));
        user.setCurrentBonusPoints(safeSubtract(user.getCurrentBonusPoints(), order.getBonusWon()));
        user.setTotalSaved(safeSubtract(user.getTotalSaved(), order.getTotalSaved()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void updateUserAfterOrder(Order order, User user) {
        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setBonusPointsWon(safeAdd(user.getBonusPointsWon(), order.getBonusWon()));
        user.setCurrentBonusPoints(safeAdd(user.getCurrentBonusPoints(), order.getBonusWon()));
        user.setTotalShippingFeePaid(safeAdd(user.getTotalShippingFeePaid(), order.getShippingFee()));
        user.setTotalOrderFeePaid(safeAdd(user.getTotalOrderFeePaid(), order.getTotalPaid()));
        user.setTotalSaved(safeAdd(user.getTotalSaved(), order.getTotalSaved()));
        userRepository.save(user);
        log.info("User updated after order: username={}, totalOrderFeePaid={}, orderTotalPaid={}",
                user.getUsername(), user.getTotalOrderFeePaid(), order.getTotalPaid());
    }

    private void validateUniquePhone(String phone) {
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new EmailAlreadyExistException("Phone already exists!");
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistException("Email already exists!");
        }
    }

    private BigDecimal safeSubtract(BigDecimal value, BigDecimal subtractor) {
        return value != null && subtractor != null ? value.subtract(subtractor) : value;
    }

    private BigDecimal safeAdd(BigDecimal value, BigDecimal addend) {
        return value != null && addend != null ? value.add(addend) : value;
    }
}