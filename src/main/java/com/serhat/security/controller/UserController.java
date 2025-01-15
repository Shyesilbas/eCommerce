package com.serhat.security.controller;

import com.serhat.security.dto.response.UserResponse;
import com.serhat.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        UserResponse userResponse = userService.userInfo(request, response);
        return ResponseEntity.ok(userResponse);
    }
}
