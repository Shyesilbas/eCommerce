package com.serhat.security.service;

import com.serhat.security.dto.response.UserResponse;
import com.serhat.security.entity.User;
import com.serhat.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

}
