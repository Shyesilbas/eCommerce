package com.serhat.security.service;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.*;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.entity.enums.TransactionType;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.AddressRepository;
import com.serhat.security.repository.TransactionRepository;
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
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;
    private final TokenInterface tokenInterface;
    private final TransactionRepository transactionRepository;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateUserRegistration(request);

        User user = buildUserFromRequest(request);
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
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistException("Email Exists!");
        }
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExists("Username exists!");
        }
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new UsernameAlreadyExists("Phone exists!");
        }
    }

    @Transactional
    public UpdateMembershipPlan updateMembershipPlan(HttpServletRequest servletRequest, UpdateMembershipRequest request) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        MembershipPlan currentPlan = user.getMembershipPlan();

        if (request.membershipPlan() == null) {
            throw new NullRequestException("Membership plan cannot be null.");
        }
        if (currentPlan.equals(request.membershipPlan())) {
            throw new SamePlanRequestException("You requested the same plan that you currently have.");
        }

        BigDecimal fee = request.membershipPlan().getFee();
        Wallet wallet = user.getWallet();
        String paymentMessage = "Membership plan updated successfully.";

        if (request.paymentMethod().equals(PaymentMethod.E_WALLET)) {
            if (wallet == null) {
                throw new WalletNotFoundException(user.getUsername() + " does not have a Wallet!");
            }
            if (wallet.getBalance().compareTo(fee) < 0) {
                throw new InsufficientFundsException("Insufficient funds in E-Wallet!");
            }

            wallet.setBalance(wallet.getBalance().subtract(fee));
            Transaction transaction = new Transaction();
            transaction.setWallet(wallet);
            transaction.setUser(user);
            transaction.setOrder(null);
            transaction.setAmount(fee);
            transaction.setTransactionType(TransactionType.PAYMENT);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setDescription("Membership plan payment via E-Wallet");
            transactionRepository.save(transaction);

            paymentMessage = "Membership plan updated successfully. Payment made via E-Wallet.";
        }

        user.setMembershipPlan(request.membershipPlan());
        log.info("Membership plan updated for {} with payment method: {}", user.getUsername(), request.paymentMethod());

        return new UpdateMembershipPlan(request.membershipPlan(), fee, paymentMessage);
    }



    private User buildUserFromRequest(RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .email(request.email())
                .role(request.role())
                .bonusPointsWon(new BigDecimal("0.0"))
                .totalOrders(0)
                .cancelledOrders(0)
                .membershipPlan(MembershipPlan.BASIC)
                .totalOrderFeePaid(BigDecimal.ZERO)
                .totalShippingFeePaid(BigDecimal.ZERO)
                .activeDiscountCode(false)
                .build();

        if (request.address() != null && !request.address().isEmpty()) {
            List<Address> addresses = request.address().stream()
                    .map(addressDto -> Address.builder()
                            .country(addressDto.getCountry())
                            .city(addressDto.getCity())
                            .street(addressDto.getStreet())
                            .aptNo(addressDto.getAptNo())
                            .flatNo(addressDto.getFlatNo())
                            .description(addressDto.getDescription())
                            .addressType(addressDto.getAddressType())
                            .user(user)
                            .build())
                    .toList();

            user.setAddresses(addresses);
        }

        return user;
    }

    private void saveRawPassword(String message, String username, String rawPassword) {
        String filePath = "raw_credentials.txt";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
            bufferedWriter.write(message + " Username: " + username + " Raw Password: " + rawPassword);
            bufferedWriter.newLine();
            log.info("WRITTEN TO FILE");
        } catch (IOException e) {
            log.error("Error writing to file: " + e.getMessage());
        }
    }

    @Transactional
    public UpdatePhoneResponse updatePhone(HttpServletRequest request, UpdatePhoneRequest updatePhoneRequest) {
        User user = tokenInterface.getUserFromToken(request);

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
        User user = tokenInterface.getUserFromToken(request);

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
        User user = tokenInterface.getUserFromToken(request);

        log.info("User details: userId={}, email={}, username={}, role={}, password={}, total orders = {}",
                user.getUserId(), user.getEmail(), user.getUsername(), user.getRole(), user.getPassword(), user.getTotalOrders());

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .password(user.getPassword())
                .totalOrders(user.getTotalOrders())
                .cancelledOrders(user.getCancelledOrders())
                .bonusPoints(user.getBonusPointsWon())
                .membershipPlan(user.getMembershipPlan())
                .role(user.getRole())
                .totalOrderFeePaid(user.getTotalOrderFeePaid())
                .totalShippingFeePaid(user.getTotalShippingFeePaid())
                .build();
    }

    public List<AddressResponse> addressInfo(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<Address> addresses = addressRepository.findByUser_Username(user.getUsername());

        if (addresses.isEmpty()) {
            throw new RuntimeException("No addresses found for user: " + user.getUsername());
        }

        return addresses.stream()
                .map(address -> AddressResponse.builder()
                        .addressId(address.getAddressId())
                        .country(address.getCountry())
                        .city(address.getCity())
                        .street(address.getStreet())
                        .aptNo(address.getAptNo())
                        .flatNo(address.getFlatNo())
                        .description(address.getDescription())
                        .addressType(address.getAddressType())
                        .build())
                .toList();
    }


    @Transactional
    public UpdateAddressResponse updateAddress(Long addressId, HttpServletRequest request, UpdateAddressRequest updateAddressRequest) {
        User user = tokenInterface.getUserFromToken(request);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new RuntimeException("Address does not belong to the user: " + user.getUsername());
        }

        if (updateAddressRequest.country() != null) {
            address.setCountry(updateAddressRequest.country());
        }
        if (updateAddressRequest.city() != null) {
            address.setCity(updateAddressRequest.city());
        }
        if (updateAddressRequest.street() != null) {
            address.setStreet(updateAddressRequest.street());
        }
        if (updateAddressRequest.aptNo() != null) {
            address.setAptNo(updateAddressRequest.aptNo());
        }
        if (updateAddressRequest.flatNo() != null) {
            address.setFlatNo(updateAddressRequest.flatNo());
        }
        if (updateAddressRequest.description() != null) {
            address.setDescription(updateAddressRequest.description());
        }
        if (updateAddressRequest.addressType() != null) {
            address.setAddressType(updateAddressRequest.addressType());
        }

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
        User user = tokenInterface.getUserFromToken(request);

        AddressDto addressDto = addAddressRequest.addressDto();
        Address newAddress = Address.builder()
                .country(addressDto.country())
                .city(addressDto.city())
                .street(addressDto.street())
                .aptNo(addressDto.aptNo())
                .flatNo(addressDto.flatNo())
                .description(addressDto.description())
                .addressType(addressDto.addressType())
                .user(user)
                .build();

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
        User user = tokenInterface.getUserFromToken(request);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new RuntimeException("Address does not belong to the user: " + user.getUsername());
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