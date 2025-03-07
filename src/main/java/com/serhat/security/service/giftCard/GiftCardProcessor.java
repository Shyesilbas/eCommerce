package com.serhat.security.service.giftCard;

import com.serhat.security.component.mapper.GiftCardMapper;
import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import com.serhat.security.service.payment.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardProcessor {
    private final GiftCardValidationService giftCardValidationService;
    private final TransactionService transactionService;
    private final GiftCardMapper giftCardMapper;

    @Transactional
    public GiftCard applyGiftCard(OrderRequest orderRequest, BigDecimal totalPrice) {
        if (orderRequest.giftCardId() == null) {
            return null;
        }
        GiftCard giftCard = giftCardValidationService.findGiftCardById(orderRequest.giftCardId());
        giftCardValidationService.validateGiftCardStatusAndAmount(giftCard);
        giftCardValidationService.compareGiftCardAmountWithOrderPrice(giftCard, totalPrice);
        giftCard.setStatus(CouponStatus.USED);
        giftCardValidationService.saveGiftCard(giftCard);
        return giftCard;
    }

    @Transactional
    public GiftCardDto generateGiftCard(User user, GiftAmount requestedAmount) {
        GiftCard giftCard = giftCardMapper.toGiftCard(user, requestedAmount);
        transactionService.createGiftCardTransaction(user, requestedAmount.getAmount());
        giftCardValidationService.saveGiftCard(giftCard);
        return giftCardMapper.toGiftCardDto(giftCard);
    }
}
