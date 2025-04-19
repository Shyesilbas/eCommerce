package com.serhat.ecommerce.discount.giftCard.mapper;

import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.discount.dto.object.GiftCardDto;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.enums.GiftAmount;
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
