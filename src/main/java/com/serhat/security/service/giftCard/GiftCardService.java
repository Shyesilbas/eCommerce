package com.serhat.security.service.giftCard;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GiftCardService {
    GiftCardDto generateGiftCard(GiftAmount requestedAmount);
    Page<GiftCardDto> getGiftCardsByStatus(CouponStatus status, Pageable pageable);
    GiftCard findById(Long giftCardId);
}