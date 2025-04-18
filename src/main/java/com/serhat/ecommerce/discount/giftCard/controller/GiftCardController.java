package com.serhat.ecommerce.discount.giftCard.controller;

import com.serhat.ecommerce.dto.object.GiftCardDto;
import com.serhat.ecommerce.dto.request.GenerateGiftCardRequest;
import com.serhat.ecommerce.enums.CouponStatus;
import com.serhat.ecommerce.discount.giftCard.service.GiftCardService;
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
    public ResponseEntity<Page<GiftCardDto>> getAvailableGiftCards(
           Pageable pageable , @RequestParam CouponStatus couponStatus) {
        Page<GiftCardDto> availableGiftCards = giftCardService.getGiftCardsByStatus(couponStatus ,pageable);
        return ResponseEntity.ok(availableGiftCards);
    }

}
