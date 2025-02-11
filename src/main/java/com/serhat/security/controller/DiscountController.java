package com.serhat.security.controller;

import com.serhat.security.dto.response.AvailableDiscountResponse;
import com.serhat.security.dto.response.ExpiredDiscountResponse;
import com.serhat.security.dto.response.UsedDiscountResponse;
import com.serhat.security.entity.DiscountCode;
import com.serhat.security.service.discountService.DiscountCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<AvailableDiscountResponse>> getAvailableDiscountCodes(
            HttpServletRequest request,
             Pageable pageable) {
        return ResponseEntity.ok(discountCodeService.getAvailableDiscountCodes(request, pageable));
    }

    @GetMapping("/used")
    public ResponseEntity<Page<UsedDiscountResponse>> getUsedDiscountCodes(
            HttpServletRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(discountCodeService.getUsedDiscountCodes(request, pageable));
    }

    @GetMapping("/expired")
    public ResponseEntity<Page<ExpiredDiscountResponse>> getExpiredDiscountCodes(
            HttpServletRequest request,
             Pageable pageable) {
        return ResponseEntity.ok(discountCodeService.getExpiredDiscountCodes(request, pageable));
    }

}
