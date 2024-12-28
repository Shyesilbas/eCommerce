package com.serhat.security.service;

import com.serhat.security.dto.request.RegisterRequest;
import com.serhat.security.dto.response.RegisterResponse;
import com.serhat.security.dto.response.UserResponse;
import com.serhat.security.entity.User;
import com.serhat.security.exception.EmailAlreadyExistException;
import com.serhat.security.exception.UsernameAlreadyExists;
import com.serhat.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request){

        boolean isEmailExists = userRepository.findByEmail(request.email()).isPresent();
        if(isEmailExists){
            throw new EmailAlreadyExistException("Email  Exists!");
        }
        boolean isUsernameExists = userRepository.findByUsername(request.username()).isPresent();
        if (isUsernameExists){
            throw new UsernameAlreadyExists("Username exists!");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .role(request.role())
                .build();

        userRepository.save(user);
        return new RegisterResponse(
                "Register Successful! Now you can login with your credentials.",
                user.getUsername(),
                user.getEmail(),
                LocalDateTime.now()
        );
    }

    public UserResponse userInfo(Principal principal){
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(username+" not found"));

        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

}
