package com.serhat.security.controller;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.GenerateGiftCardRequest;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.service.giftCard.GiftCardService;
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
    public GiftCardDto generateGiftCard( @RequestBody GenerateGiftCardRequest requestBody) {
        return giftCardService.generateGiftCard(requestBody.amount());
    }
    @GetMapping("/getByStatus")
    public ResponseEntity<Page<GiftCardResponse>> getAvailableGiftCards(
           Pageable pageable , @RequestParam CouponStatus couponStatus) {
        Page<GiftCardResponse> availableGiftCards = giftCardService.getGiftCardsByStatus(couponStatus ,pageable);
        return ResponseEntity.ok(availableGiftCards);
    }

}
