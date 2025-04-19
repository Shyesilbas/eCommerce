package com.serhat.ecommerce.discount.discountService.controller;

import com.serhat.ecommerce.discount.discountService.entity.DiscountCode;
import com.serhat.ecommerce.discount.discountService.service.DiscountCodeService;
import com.serhat.ecommerce.discount.dto.response.AvailableDiscountResponse;
import com.serhat.ecommerce.discount.dto.response.ExpiredDiscountResponse;
import com.serhat.ecommerce.discount.dto.response.UsedDiscountResponse;
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
    public ResponseEntity<DiscountCode> generateDiscountCode() {
        DiscountCode discountCode = discountCodeService.generateDiscountCode();
        return ResponseEntity.ok(discountCode);
    }

    @GetMapping("/available")
    public ResponseEntity<Page<AvailableDiscountResponse>> getAvailableDiscountCodes(
             Pageable pageable) {
        return ResponseEntity.ok(discountCodeService.getAvailableDiscountCodes( pageable));
    }

    @GetMapping("/used")
    public ResponseEntity<Page<UsedDiscountResponse>> getUsedDiscountCodes(
            Pageable pageable) {
        return ResponseEntity.ok(discountCodeService.getUsedDiscountCodes( pageable));
    }

    @GetMapping("/expired")
    public ResponseEntity<Page<ExpiredDiscountResponse>> getExpiredDiscountCodes(
             Pageable pageable) {
        return ResponseEntity.ok(discountCodeService.getExpiredDiscountCodes(pageable));
    }

}
