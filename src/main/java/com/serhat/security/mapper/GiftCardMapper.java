package com.serhat.security.mapper;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GiftCardMapper {
    public GiftCardDto toGiftCardDto(GiftCard giftCard) {
        return new GiftCardDto(
                giftCard.getId(),
                giftCard.getCode(),
                giftCard.getGiftAmount(),
                giftCard.getCreatedAt(),
                giftCard.getExpiresAt(),
                giftCard.getStatus()
        );
    }

    public GiftCardResponse toGiftCardResponse(GiftCard giftCard) {
        return new GiftCardResponse(
                giftCard.getGiftAmount(),
                giftCard.getExpiresAt(),
                giftCard.getStatus()
        );
    }

    public GiftCard toGiftCard(User user, GiftAmount giftAmount) {
        return GiftCard.builder()
                .code(UUID.randomUUID().toString())
                .user(user)
                .giftAmount(giftAmount)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusYears(1))
                .status(CouponStatus.NOT_USED)
                .build();
    }
}
