package com.serhat.security.service.giftCard;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import com.serhat.security.exception.GiftCardNotFoundException;
import com.serhat.security.component.mapper.GiftCardMapper;
import com.serhat.security.repository.GiftCardRepository;
import com.serhat.security.service.user.UserService;
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