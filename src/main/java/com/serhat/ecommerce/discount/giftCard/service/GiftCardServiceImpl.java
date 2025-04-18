package com.serhat.ecommerce.discount.giftCard.service;

import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.discount.giftCard.mapper.GiftCardMapper;
import com.serhat.ecommerce.discount.giftCard.repository.GiftCardRepository;
import com.serhat.ecommerce.dto.object.GiftCardDto;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.enums.CouponStatus;
import com.serhat.ecommerce.enums.GiftAmount;
import com.serhat.ecommerce.discount.discountException.GiftCardNotFoundException;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardServiceImpl implements GiftCardService {
    private final GiftCardRepository giftCardRepository;
    private final UserService userService;
    private final GiftCardProcessor giftCardProcessor;
    private final GiftCardMapper giftCardMapper;

    @Override
    @Transactional
    public GiftCardDto generateGiftCard(GiftAmount requestedAmount) {
        User user = userService.getAuthenticatedUser();
        return giftCardProcessor.generateGiftCard(user, requestedAmount);
    }

    @Override
    public Page<GiftCardDto> getGiftCardsByStatus(CouponStatus status, Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Page<GiftCard> giftCards = giftCardRepository.findByUserAndStatus(user, status, pageable);
        if (giftCards.isEmpty()) {
            throw new GiftCardNotFoundException("No " + status.toString().toLowerCase() + " gift cards found.");
        }
        return giftCards.map(giftCardMapper::toGiftCardDto);
    }

    @Override
    public GiftCard findById(Long giftCardId) {
        return giftCardRepository.findById(giftCardId)
                .orElseThrow(() -> new GiftCardNotFoundException("Gift card not found with ID: " + giftCardId));
    }
}