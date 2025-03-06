package com.serhat.security.controller;

import com.serhat.security.dto.request.*;
import com.serhat.security.dto.response.*;
import com.serhat.security.dto.request.AddBonusRequest;
import com.serhat.security.service.bonusStrategy.BonusService;
import com.serhat.security.service.password.PasswordService;
import com.serhat.security.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final PasswordService passwordService;
    private final BonusService bonusService;

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getUserInfo(HttpServletRequest request){
        UserResponse userResponse = userService.userInfo(request);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/bonusInfo")
    public ResponseEntity<BonusPointInformation> bonusPointInfo(HttpServletRequest request){
        return ResponseEntity.ok(bonusService.bonusPointInformation(request));
    }

    @GetMapping("redisTest")
    public String redisTest() throws InterruptedException{
        Thread.sleep(5000L);
        return "Should response after 5 seconds every time.";
    }

    @PostMapping("/update-password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            HttpServletRequest servletRequest,
            @RequestBody UpdatePasswordRequest request) {

        try {
            UpdatePasswordResponse updatePasswordResponse = passwordService.updatePassword(servletRequest,  request);
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
            ForgotPasswordResponse forgotPasswordResponse = passwordService.forgotPassword(request);
            return ResponseEntity.ok(forgotPasswordResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ForgotPasswordResponse(e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/update-Email")
    public ResponseEntity<UpdateEmailResponse> updatePassword(
            HttpServletRequest servletRequest,
            @RequestBody UpdateEmailRequest request) {

        try {
            UpdateEmailResponse updateEmailResponse = userService.updateEmail(servletRequest,  request);
            return ResponseEntity.ok(updateEmailResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new UpdateEmailResponse("Error: " + e.getMessage(),null ,LocalDateTime.now())
            );
        }
    }

    @PutMapping("/update-phone")
    public ResponseEntity<UpdatePhoneResponse> updatePhone(@Valid @RequestBody UpdatePhoneRequest phoneRequest ,
                                                           HttpServletRequest servletRequest ){
        return ResponseEntity.ok(userService.updatePhone(servletRequest,phoneRequest));
    }

    @PutMapping("/update-membership")
    public ResponseEntity<UpdateMembershipPlan> updateMembership(@RequestBody UpdateMembershipRequest request,
                                                           HttpServletRequest servletRequest ){
        return ResponseEntity.ok(userService.updateMembershipPlan(servletRequest,request));
    }

    @PostMapping("/add-bonus")
    public ResponseEntity<AddBonusResponse> addAddress(@RequestBody AddBonusRequest addBonusRequest  , HttpServletRequest request){
        return ResponseEntity.ok(bonusService.addBonus(request, addBonusRequest));
    }

}
