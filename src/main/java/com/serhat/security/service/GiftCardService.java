package com.serhat.security.service;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import com.serhat.security.exception.InvalidGiftAmountException;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.GiftCardMapper;
import com.serhat.security.repository.GiftCardRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardService {
    private final GiftCardRepository repository;
    private final TokenInterface tokenInterface;
    private final GiftCardMapper giftCardMapper;

    @Transactional
    public GiftCardDto generateGiftCard(HttpServletRequest request, int requestedAmount) {
        User user = tokenInterface.getUserFromToken(request);
        GiftAmount giftAmount = getValidGiftAmount(requestedAmount);
        GiftCard giftCard = giftCardMapper.toGiftCard(user,giftAmount);
        repository.save(giftCard);
        return giftCardMapper.toGiftCardDto(giftCard);
    }

    public List<GiftCardDto> getAvailableGiftCards(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<GiftCard> availableGiftCards = repository.findByUserAndStatus(user, CouponStatus.NOT_USED);
        return availableGiftCards.stream()
                .map(giftCardMapper::toGiftCardDto)
                .collect(Collectors.toList());
    }

    public List<GiftCardDto> getUsedGiftCards(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<GiftCard> usedGiftCards = repository.findByUserAndStatus(user, CouponStatus.USED);
        return usedGiftCards.stream()
                .map(giftCardMapper::toGiftCardDto)
                .collect(Collectors.toList());
    }
    private GiftAmount getValidGiftAmount(int amount) {
        for (GiftAmount giftAmount : GiftAmount.values()) {
            if (giftAmount.getAmount() == amount) {
                return giftAmount;
            }
        }
        throw new InvalidGiftAmountException("Invalid gift amount: " + amount);
    }



}
