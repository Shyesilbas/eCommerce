package com.serhat.security.service;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.*;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.exception.EmailAlreadyExistException;
import com.serhat.security.exception.UsernameAlreadyExists;
import com.serhat.security.jwt.JwtUtil;
import com.serhat.security.repository.AddressRepository;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AddressRepository addressRepository;
    public final AuthService authService;
    private final NotificationService notificationService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        boolean isEmailExists = userRepository.findByEmail(request.email()).isPresent();
        if (isEmailExists) {
            throw new EmailAlreadyExistException("Email Exists!");
        }
        boolean isUsernameExists = userRepository.findByUsername(request.username()).isPresent();
        if (isUsernameExists) {
            throw new UsernameAlreadyExists("Username exists!");
        }

        boolean isPhoneExists = userRepository.findByPhone(request.phone()).isPresent();
        if (isPhoneExists) {
            throw new UsernameAlreadyExists("Phone exists!");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .email(request.email())
                .role(request.role())
                .totalOrders(0)
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

        saveRawPassword("Register",user.getUsername(),request.password());
        userRepository.save(user);

        return new RegisterResponse(
                "Register Successful! Now you can login with your credentials.",
                user.getUsername(),
                user.getEmail(),
                LocalDateTime.now()
        );
    }

    private void saveRawPassword(String message ,String username , String rawPassword){
        String filePath = "raw_credentials.txt";
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath,true))){
            bufferedWriter.write(message + "Username : "+username + " Raw Password : "+ rawPassword);
            bufferedWriter.newLine();
            log.info("WRITTEN TO FILE");
        }catch (IOException e){
            log.error("Error writing to file : "+e.getMessage());
        }
    }

    @Transactional
    public UpdatePhoneResponse updatePhone(HttpServletRequest request, HttpServletResponse response, UpdatePhoneRequest updatePhoneRequest) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        Optional<User> existingUserWithPhone = userRepository.findByPhone(updatePhoneRequest.phone());
        if (existingUserWithPhone.isPresent()) {
            throw new EmailAlreadyExistException("Phone already exists!");
        }

        notificationService.addNotification(request, NotificationTopic.PHONE_UPDATE);
        user.setPhone(updatePhoneRequest.phone());
        userRepository.save(user);

        return new UpdatePhoneResponse("Email updated successfully.", user.getPhone() ,LocalDateTime.now());
    }

    @Transactional
    public UpdateEmailResponse updateEmail(HttpServletRequest request, HttpServletResponse response, UpdateEmailRequest updateEmailRequest) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        Optional<User> existingUserWithNewEmail = userRepository.findByEmail(updateEmailRequest.newEmail());
        if (existingUserWithNewEmail.isPresent()) {
            throw new EmailAlreadyExistException("Email already exists!");
        }

        notificationService.addNotification(request,NotificationTopic.EMAIL_UPDATE);
        user.setEmail(updateEmailRequest.newEmail());
        userRepository.save(user);

        return new UpdateEmailResponse("Email updated successfully.", user.getEmail() ,LocalDateTime.now());
    }

    @Transactional

    public UpdatePasswordResponse updatePassword(HttpServletRequest servletRequest ,HttpServletResponse response ,UpdatePasswordRequest request){
        String token = extractTokenFromRequest(servletRequest);
        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        String currentPassword = user.getPassword();
        if(currentPassword.equals(request.newPassword())){
            throw new RuntimeException("Passwords are same.");
        }
        if (!passwordEncoder.matches(request.oldPassword(), currentPassword)) {
            throw new RuntimeException("Old password is incorrect.");
        }
        notificationService.addNotification(servletRequest,NotificationTopic.PASSWORD_UPDATE);
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        saveRawPassword("Password Update",username,request.newPassword());
        return new UpdatePasswordResponse("Password updated successfully.",LocalDateTime.now());
    }

    @Transactional

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + request.email()));

            String currentPassword = user.getPassword();
            if (currentPassword.equals(request.newPassword())) {
                throw new RuntimeException("New password must be different from the current password.");
            }

            user.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
            saveRawPassword("Forgot Password",user.getUsername(), request.newPassword());
            return new ForgotPasswordResponse("Password updated successfully.", LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset password: " + e.getMessage());
        }
    }


    public UserResponse userInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromRequest(request);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        log.info("User details: userId={}, email={}, username={}, role={}, password={}, total orders = {}",
                user.getUserId(), user.getEmail(), user.getUsername(), user.getRole(), user.getPassword(),user.getTotalOrders());

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .password(user.getPassword())
                .totalOrders(user.getTotalOrders())
                .role(user.getRole())
                .build();
    }

    public List<AddressResponse> addressInfo(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        List<Address> addresses = addressRepository.findByUser_Username(username);

        if (addresses.isEmpty()) {
            throw new RuntimeException("No addresses found for user: " + username);
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
    public AddAddressResponse addAddress(HttpServletRequest servletRequest, AddAddressRequest request) {
        String token = extractTokenFromRequest(servletRequest);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        AddressDto addressDto = request.addressDto();
        Address newAddress = Address.builder()
                .country(addressDto.country())
                .city(addressDto.city())
                .street(addressDto.street())
                .aptNo(addressDto.aptNo())
                .flatNo(addressDto.flatNo())
                .description(addressDto.description())
                .addressType(addressDto.addressType())
                .user(userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")))
                .build();

        addressRepository.save(newAddress);
        notificationService.addNotification(servletRequest,NotificationTopic.ADDRESS_ADDED);

        return new AddAddressResponse(
                "Address added successfully",
                newAddress.getAddressId(),
                LocalDateTime.now(),
                newAddress.getDescription()
        );
    }

    @Transactional
    public DeleteAddressResponse deleteAddress(Long addressId, HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token == null) {
            throw new RuntimeException("Token not found in request");
        }

        String username = jwtUtil.extractUsername(token);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Address does not belong to the user: " + username);
        }

        addressRepository.delete(address);
        notificationService.addNotification(request,NotificationTopic.ADDRESS_DELETED);

        return new DeleteAddressResponse(
                addressId,
                "Address Deleted Successfully",
                LocalDateTime.now()
        );
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return token;
    }


}
