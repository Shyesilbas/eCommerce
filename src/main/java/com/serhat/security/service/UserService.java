package com.serhat.security.service;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.*;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.AddressMapper;
import com.serhat.security.mapper.UserMapper;
import com.serhat.security.repository.AddressRepository;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;
    private final TokenInterface tokenInterface;
    private final AddressMapper addressMapper;
    private final TransactionService transactionService;
    private final UserMapper userMapper;

    public User getUserFromToken(HttpServletRequest request){
        return tokenInterface.getUserFromToken(request);
    }
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateUserRegistration(request);

        User user = userMapper.toUser(request);
        saveRawPassword("Register", user.getUsername(), request.password());
        userRepository.save(user);

        return new RegisterResponse(
                "Register Successful! Now you can login with your credentials.",
                user.getUsername(),
                user.getEmail(),
                user.getMembershipPlan(),
                LocalDateTime.now()
        );
    }

    private void validateUserRegistration(RegisterRequest request) {
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

    public BonusPointInformation bonusPointInformation(HttpServletRequest request){
        User user = getUserFromToken(request);
        return new BonusPointInformation(
                user.getBonusPointsWon(),
                user.getCurrentBonusPoints()
        );
    }

    @Transactional
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


    private void saveRawPassword(String message, String username, String rawPassword) {
        String filePath = "raw_credentials.txt";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
            bufferedWriter.write(message + " Username: " + username + " Raw Password: " + rawPassword);
            bufferedWriter.newLine();
            log.info("Raw password written to file.");
        } catch (IOException e) {
            log.error("Error writing to file: " + e.getMessage());
        }
    }

    @Transactional
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

    @Transactional
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
        saveRawPassword("Password Update", user.getUsername(), updatePasswordRequest.newPassword());

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
        saveRawPassword("Forgot Password", user.getUsername(), request.newPassword());

        return new ForgotPasswordResponse("Password updated successfully.", LocalDateTime.now());
    }

    public UserResponse userInfo(HttpServletRequest request) {
        User user = getUserFromToken(request);
        log.info("User details: userId={}, email={}, username={}, role={}, total orders={}",
                user.getUserId(), user.getEmail(), user.getUsername(), user.getRole(), user.getTotalOrders());

        return userMapper.toUserResponse(user);
    }

    public List<AddressResponse> addressInfo(HttpServletRequest request) {
        User user = getUserFromToken(request);
        List<Address> addresses = addressRepository.findByUser_Username(user.getUsername());

        if (addresses.isEmpty()) {
            throw new RuntimeException("No addresses found for user: " + user.getUsername());
        }
        return addressMapper.toAddressResponseList(addresses);
    }

    @Transactional
    public UpdateAddressResponse updateAddress(Long addressId, HttpServletRequest request, UpdateAddressRequest updateAddressRequest) {
        User user = getUserFromToken(request);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new UnauthorizedAccessException("Address does not belong to the user: " + user.getUsername());
        }

        addressMapper.updateAddressFromDto(address, updateAddressRequest);
        addressRepository.save(address);
        notificationService.addNotification(request, NotificationTopic.ADDRESS_UPDATED);

        return new UpdateAddressResponse(
                "Address updated successfully",
                address.getAddressId(),
                LocalDateTime.now(),
                address.getDescription()
        );
    }

    @Transactional
    public AddAddressResponse addAddress(HttpServletRequest request, AddAddressRequest addAddressRequest) {
        User user = getUserFromToken(request);
        AddressDto addressDto = addAddressRequest.addressDto();
        Address newAddress = addressMapper.toAddress(addressDto, user);

        addressRepository.save(newAddress);
        notificationService.addNotification(request, NotificationTopic.ADDRESS_ADDED);

        return new AddAddressResponse(
                "Address added successfully",
                newAddress.getAddressId(),
                LocalDateTime.now(),
                newAddress.getDescription()
        );
    }


    @Transactional
    public DeleteAddressResponse deleteAddress(Long addressId, HttpServletRequest request) {
        User user = getUserFromToken(request);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new UnauthorizedAccessException("Address does not belong to the user: " + user.getUsername());
        }

        addressRepository.delete(address);
        notificationService.addNotification(request, NotificationTopic.ADDRESS_DELETED);

        return new DeleteAddressResponse(
                addressId,
                "Address Deleted Successfully",
                LocalDateTime.now()
        );
    }
}
