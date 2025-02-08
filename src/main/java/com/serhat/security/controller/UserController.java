package com.serhat.security.controller;

import com.serhat.security.dto.request.*;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.enums.MembershipPlan;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.exception.AddBonusRequest;
import com.serhat.security.exception.InvalidPasswordException;
import com.serhat.security.exception.UserNotFoundException;
import com.serhat.security.service.AuthService;
import com.serhat.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getUserInfo(HttpServletRequest request) throws InterruptedException{
        UserResponse userResponse = userService.userInfo(request);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/bonusInfo")
    public ResponseEntity<BonusPointInformation> bonusPointInfo(HttpServletRequest request)throws InterruptedException{
        return ResponseEntity.ok(userService.bonusPointInformation(request));
    }

    @GetMapping("redisTest")
    public String redisTest() throws InterruptedException{
        Thread.sleep(5000L);
        return "Should response after 5 seconds every time.";
    }

    @GetMapping("/addressInfo")
    public ResponseEntity<Page<AddressResponse>> getAddressInfo(
            HttpServletRequest request,
            @RequestParam int page,
            @RequestParam int size) {
        Page<AddressResponse> addressResponse = userService.addressInfo(request, page, size);
        return ResponseEntity.ok(addressResponse);
    }


    @PutMapping("/update-address")
    public ResponseEntity<UpdateAddressResponse> updateAddress(
            @RequestParam Long addressId,
            @RequestBody UpdateAddressRequest updateAddressRequest,
            HttpServletRequest request
    ) {
        UpdateAddressResponse response = userService.updateAddress(addressId, request, updateAddressRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            HttpServletRequest servletRequest,
            HttpServletResponse response,
            @RequestBody UpdatePasswordRequest request) {

        try {
            UpdatePasswordResponse updatePasswordResponse = userService.updatePassword(servletRequest,  request);
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


    @PostMapping("/add-address")
    public ResponseEntity<AddAddressResponse> addAddress(@RequestBody AddAddressRequest request , HttpServletRequest servletRequest){
        return ResponseEntity.ok(userService.addAddress(servletRequest, request));
    }

    @PostMapping("/add-bonus")
    public ResponseEntity<AddBonusResponse> addAddress(@RequestBody AddBonusRequest addBonusRequest  , HttpServletRequest request){
        return ResponseEntity.ok(userService.addBonus(request, addBonusRequest));
    }

    @DeleteMapping("/delete-address")
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestParam Long addressId , HttpServletRequest request){
        return ResponseEntity.ok(userService.deleteAddress(addressId, request));
    }

}
