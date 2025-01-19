package com.serhat.security.controller;

import com.serhat.security.dto.request.ForgotPasswordRequest;
import com.serhat.security.dto.request.UpdateEmailRequest;
import com.serhat.security.dto.request.UpdatePasswordRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.exception.InvalidPasswordException;
import com.serhat.security.exception.UserNotFoundException;
import com.serhat.security.service.AuthService;
import com.serhat.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        UserResponse userResponse = userService.userInfo(request, response);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/addressInfo")
    public ResponseEntity<List<AddressResponse>> getAddressInfo(HttpServletRequest request){
        List<AddressResponse> addressResponse = userService.addressInfo(request);
        return ResponseEntity.ok(addressResponse);
    }

    @PostMapping("/update-password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            HttpServletRequest servletRequest,
            HttpServletResponse response,
            @RequestBody UpdatePasswordRequest request) {

        try {
            UpdatePasswordResponse updatePasswordResponse = userService.updatePassword(servletRequest, response, request);
            return ResponseEntity.ok(updatePasswordResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new UpdatePasswordResponse("Error: " + e.getMessage(), LocalDateTime.now())
            );
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest request ) {
        try {
            ForgotPasswordResponse forgotPasswordResponse = userService.forgotPassword(request);
            return ResponseEntity.ok(forgotPasswordResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ForgotPasswordResponse(e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/update-Email")
    public ResponseEntity<UpdateEmailResponse> updatePassword(
            HttpServletRequest servletRequest,
            HttpServletResponse response,
            @RequestBody UpdateEmailRequest request) {

        try {
            UpdateEmailResponse updateEmailResponse = userService.updateEmail(servletRequest, response, request);
            return ResponseEntity.ok(updateEmailResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new UpdateEmailResponse("Error: " + e.getMessage(),null ,LocalDateTime.now())
            );
        }
    }

}
