package com.serhat.security.controller;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.GenerateGiftCardRequest;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.service.GiftCardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/giftCard")
@RequiredArgsConstructor
public class GiftCardController {
    private final GiftCardService giftCardService;

    @PostMapping("/generate")
    public GiftCardDto generateGiftCard(HttpServletRequest request, @RequestBody GenerateGiftCardRequest requestBody) {
        return giftCardService.generateGiftCard(request, requestBody.amount());
    }
    @GetMapping("/available")
    public ResponseEntity<Page<GiftCardResponse>> getAvailableGiftCards(
            HttpServletRequest request, Pageable pageable) {
        Page<GiftCardResponse> availableGiftCards = giftCardService.getAvailableGiftCards(request, pageable);
        return ResponseEntity.ok(availableGiftCards);
    }

    @GetMapping("/used")
    public ResponseEntity<Page<GiftCardResponse>> getUsedGiftCards(
            HttpServletRequest request, Pageable pageable) {
        Page<GiftCardResponse> usedGiftCards = giftCardService.getUsedGiftCards(request, pageable);
        return ResponseEntity.ok(usedGiftCards);
    }

}
