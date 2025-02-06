package com.serhat.security.controller;

import com.serhat.security.dto.object.GiftCardDto;
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
    public GiftCardDto generateGiftCard(HttpServletRequest request, @RequestParam int amount) {
        return giftCardService.generateGiftCard(request, amount);
    }

    @GetMapping("/available")
    public List<GiftCardDto> getAvailableGiftCards(HttpServletRequest request) {
        return giftCardService.getAvailableGiftCards(request);
    }

    @GetMapping("/used")
    public List<GiftCardDto> getUsedGiftCards(HttpServletRequest request) {
        return giftCardService.getUsedGiftCards(request);
    }
}
