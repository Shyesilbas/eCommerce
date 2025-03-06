package com.serhat.security.service.giftCard;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface GiftCardService {
    GiftCard applyGiftCard(OrderRequest orderRequest, BigDecimal totalPrice);
    GiftCardDto generateGiftCard(GiftAmount requestedAmount);
    Page<GiftCardResponse> getGiftCardsByStatus(CouponStatus status, Pageable pageable);

}
