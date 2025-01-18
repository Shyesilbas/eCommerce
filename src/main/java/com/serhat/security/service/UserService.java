package com.serhat.security.service;

import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.dto.response.RegisterResponse;
import com.serhat.security.dto.response.UserResponse;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
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

        userRepository.save(user);

        return new RegisterResponse(
                "Register Successful! Now you can login with your credentials.",
                user.getUsername(),
                user.getEmail(),
                LocalDateTime.now()
        );
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
