package com.serhat.ecommerce.user.userS.controller;

import com.serhat.ecommerce.auth.dto.request.ForgotPasswordRequest;
import com.serhat.ecommerce.auth.dto.request.UpdatePasswordRequest;
import com.serhat.ecommerce.auth.dto.response.ForgotPasswordResponse;
import com.serhat.ecommerce.auth.dto.response.UpdatePasswordResponse;
import com.serhat.ecommerce.discount.dto.response.AddBonusResponse;
import com.serhat.ecommerce.discount.dto.response.BonusPointInformation;
import com.serhat.ecommerce.discount.bonus.dto.AddBonusRequest;
import com.serhat.ecommerce.discount.bonus.service.BonusService;
import com.serhat.ecommerce.auth.password.PasswordService;
import com.serhat.ecommerce.user.userS.dto.*;
import com.serhat.ecommerce.user.userS.service.UserService;
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
    public ResponseEntity<UserResponse> getUserInfo(){
        UserResponse userResponse = userService.userInfo();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/bonusInfo")
    public ResponseEntity<BonusPointInformation> bonusPointInfo(){
        return ResponseEntity.ok(bonusService.bonusPointInformation());
    }

    @GetMapping("redisTest")
    public String redisTest() throws InterruptedException{
        Thread.sleep(5000L);
        return "Should response after 5 seconds every time.";
    }

    @PostMapping("/update-password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @RequestBody UpdatePasswordRequest request) {

        try {
            UpdatePasswordResponse updatePasswordResponse = passwordService.updatePassword(request);
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
    public ResponseEntity<UpdateEmailResponse> updateEmail(
            @RequestBody UpdateEmailRequest request) {

        try {
            UpdateEmailResponse updateEmailResponse = userService.updateEmail(request);
            return ResponseEntity.ok(updateEmailResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new UpdateEmailResponse("Error: " + e.getMessage(),null ,LocalDateTime.now())
            );
        }
    }

    @PutMapping("/update-phone")
    public ResponseEntity<UpdatePhoneResponse> updatePhone(@Valid @RequestBody UpdatePhoneRequest phoneRequest){
        return ResponseEntity.ok(userService.updatePhone(phoneRequest));
    }

    @PutMapping("/update-membership")
    public ResponseEntity<UpdateMembershipPlan> updateMembership(@RequestBody UpdateMembershipRequest request){
        return ResponseEntity.ok(userService.updateMembershipPlan(request));
    }

    @PostMapping("/add-bonus")
    public ResponseEntity<AddBonusResponse> addAddress(@RequestBody AddBonusRequest addBonusRequest){
        return ResponseEntity.ok(bonusService.addBonus(addBonusRequest));
    }

}
