package com.serhat.security.controller;

import com.serhat.security.dto.response.AvailableDiscountResponse;
import com.serhat.security.dto.response.ExpiredDiscountResponse;
import com.serhat.security.dto.response.UsedDiscountResponse;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.service.DiscountCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/discount")
public class DiscountController {

    private final DiscountCodeService discountCodeService;

    @PostMapping("/generate")
    public ResponseEntity<DiscountCode> generateDiscountCode(HttpServletRequest request) {
        DiscountCode discountCode = discountCodeService.generateDiscountCode(request);
        return ResponseEntity.ok(discountCode);
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableDiscountResponse>> getAvailableDiscountCodes(HttpServletRequest request) {
        List<AvailableDiscountResponse> availableDiscounts = discountCodeService.getAvailableDiscountCodes(request);
        return ResponseEntity.ok(availableDiscounts);
    }

    @GetMapping("/used")
    public ResponseEntity<List<UsedDiscountResponse>> getUsedDiscountCodes(HttpServletRequest request) {
        List<UsedDiscountResponse> usedDiscounts = discountCodeService.getUsedDiscountCodes(request);
        return ResponseEntity.ok(usedDiscounts);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<ExpiredDiscountResponse>> getExpiredDiscountCodes(HttpServletRequest request) {
        List<ExpiredDiscountResponse> expiredDiscounts = discountCodeService.getExpiredDiscountCodes(request);
        return ResponseEntity.ok(expiredDiscounts);
    }
}
