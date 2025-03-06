package com.serhat.security.service.giftCard;

import com.serhat.security.dto.object.GiftCardDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.GiftCardResponse;
import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.GiftAmount;
import com.serhat.security.exception.GiftCardNotFoundException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.component.mapper.GiftCardMapper;
import com.serhat.security.repository.GiftCardRepository;
import com.serhat.security.service.payment.TransactionService;
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
public class GiftCardServiceImpl implements GiftCardService {
    private final GiftCardRepository repository;
    private final TokenInterface tokenInterface;
    private final GiftCardMapper giftCardMapper;
    private final TransactionService transactionService;
    private final GiftCardValidationService giftCardValidationService;

    public User getUserFromToken(HttpServletRequest request){
        return tokenInterface.getUserFromToken(request);
    }

    public void validateGiftCardStatusAndAmount(GiftCard giftCard){
        giftCardValidationService.validateGiftCardStatusAndAmount(giftCard);
    }

    public GiftCard findGiftCardById(Long id){
        return giftCardValidationService.findGiftCardById(id);
    }

    public void compareGiftCardAmountWithOrderPrice(GiftCard giftCard, BigDecimal totalPrice){
        giftCardValidationService.compareGiftCardAmountWithOrderPrice(giftCard, totalPrice);
    }

    @Override
    @Transactional
    public GiftCard applyGiftCard(OrderRequest orderRequest, BigDecimal totalPrice) {
        if (orderRequest.giftCardId() == null) {
            return null;
        }
        try {
            GiftCard giftCard = findGiftCardById(orderRequest.giftCardId());
            validateGiftCardStatusAndAmount(giftCard);
            compareGiftCardAmountWithOrderPrice(giftCard, totalPrice);
            giftCard.setStatus(CouponStatus.USED);
            repository.save(giftCard);
            return giftCard;
        } catch (GiftCardNotFoundException e) {
            log.warn("Gift card not found: {}", orderRequest.giftCardId());
            return null;
        }
    }

    public void createGiftCardTransaction(User user , GiftAmount requestedAmount){
        transactionService.createGiftCardTransaction(user,requestedAmount.getAmount());
    }

    @Override
    @Transactional
    public GiftCardDto generateGiftCard(HttpServletRequest request, GiftAmount requestedAmount) {
        User user = getUserFromToken(request);
        GiftCard giftCard = giftCardMapper.toGiftCard(user, requestedAmount);
        createGiftCardTransaction(user,requestedAmount); // wallet existence and balance update will be handled
        repository.save(giftCard);
        return giftCardMapper.toGiftCardDto(giftCard);
    }


    @Override
    public Page<GiftCardResponse> getGiftCardsByStatus(HttpServletRequest request, CouponStatus status, Pageable pageable) {
        User user = getUserFromToken(request);
        Page<GiftCard> giftCards = repository.findByUserAndStatus(user, status, pageable);

        if (giftCards.isEmpty()) {
            throw new GiftCardNotFoundException("No " + status.toString().toLowerCase() + " gift cards found.");
        }

        return giftCards.map(giftCardMapper::toGiftCardResponse);
    }

}
