package com.serhat.security.controller;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.GenerateGiftCardRequest;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.service.GiftCardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<GiftCardResponse> getAvailableGiftCards(HttpServletRequest request) {
        return giftCardService.getAvailableGiftCards(request);
    }

    @GetMapping("/used")
    public List<GiftCardResponse> getUsedGiftCards(HttpServletRequest request) {
        return giftCardService.getUsedGiftCards(request);
    }
}
