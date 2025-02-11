package com.serhat.security.service;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import com.serhat.security.exception.GiftCardNotFoundException;
import com.serhat.security.exception.InvalidGiftCardException;
import com.serhat.security.exception.UsedGiftCardException;
import com.serhat.security.interfaces.GiftCardInterface;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.GiftCardMapper;
import com.serhat.security.repository.GiftCardRepository;
import com.serhat.security.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardService implements GiftCardInterface {
    private final GiftCardRepository repository;
    private final TokenInterface tokenInterface;
    private final GiftCardMapper giftCardMapper;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public GiftCard applyGiftCard(OrderRequest orderRequest, BigDecimal totalPrice) {
        if (orderRequest.giftCardId() == null) {
            return null;
        }

        GiftCard giftCard = repository.findById(orderRequest.giftCardId())
                .orElseThrow(() -> new GiftCardNotFoundException("Invalid gift card"));

        validateGiftCard(giftCard);
        if (giftCard.getGiftAmount().getAmount().compareTo(totalPrice) > 0) {
            throw new InvalidGiftCardException("Gift card balance exceeds the total price.");
        }

        giftCard.setStatus(CouponStatus.USED);
        repository.save(giftCard);
        return giftCard;
    }

    @Override
    public void validateGiftCard(GiftCard giftCard) {
        if (giftCard.getStatus().equals(CouponStatus.USED)) {
            throw new UsedGiftCardException("Gift card is used!");
        }

        if (giftCard.getGiftAmount().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidGiftCardException("This gift card has no balance!");
        }
    }

    public void createGiftCardTransaction(User user , GiftAmount requestedAmount){
        transactionService.createGiftCardTransaction(user,requestedAmount.getAmount());
    }

    @Transactional
    public GiftCardDto generateGiftCard(HttpServletRequest request, GiftAmount requestedAmount) {
        User user = tokenInterface.getUserFromToken(request);
        GiftCard giftCard = giftCardMapper.toGiftCard(user, requestedAmount);
        createGiftCardTransaction(user,requestedAmount); // wallet existence and balance update will be handled
        repository.save(giftCard);
        return giftCardMapper.toGiftCardDto(giftCard);
    }


    public Page<GiftCardResponse> getAvailableGiftCards(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<GiftCard> availableGiftCards = repository.findByUserAndStatus(user, CouponStatus.NOT_USED, pageable);

        if (availableGiftCards.isEmpty()) {
            throw new GiftCardNotFoundException("No available gift cards found.");
        }

        return availableGiftCards.map(giftCardMapper::toGiftCardResponse);
    }

    public Page<GiftCardResponse> getUsedGiftCards(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<GiftCard> usedGiftCards = repository.findByUserAndStatus(user, CouponStatus.USED, pageable);

        if (usedGiftCards.isEmpty()) {
            throw new GiftCardNotFoundException("No used gift cards found.");
        }

        return usedGiftCards.map(giftCardMapper::toGiftCardResponse);
    }

}
