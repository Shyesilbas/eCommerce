package com.serhat.ecommerce.discount.giftCard.service;

import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.discount.dto.object.GiftCardDto;
import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.enums.GiftAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GiftCardService {
    GiftCardDto generateGiftCard(GiftAmount requestedAmount);
    Page<GiftCardDto> getGiftCardsByStatus(CouponStatus status, Pageable pageable);
    GiftCard findById(Long giftCardId);
}