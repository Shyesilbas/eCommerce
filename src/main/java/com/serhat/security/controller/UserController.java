package com.serhat.security.controller;

import com.serhat.security.dto.response.UserResponse;
import com.serhat.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponse> userInformation(Principal principal){
        log.debug("User information request received from : "+principal.getName());
        return ResponseEntity.ok(userService.userInfo(principal));
    }
}
