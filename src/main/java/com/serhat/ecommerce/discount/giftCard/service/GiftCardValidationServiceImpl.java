package com.serhat.ecommerce.discount.giftCard.service;

import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.discount.giftCard.repository.GiftCardRepository;
import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.discountException.GiftCardNotFoundException;
import com.serhat.ecommerce.discount.discountException.InvalidGiftCardException;
import com.serhat.ecommerce.discount.discountException.UsedGiftCardException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class GiftCardValidationServiceImpl implements GiftCardValidationService {
    private final GiftCardRepository giftCardRepository;
    @Override
    public void validateGiftCardStatusAndAmount(GiftCard giftCard) {
        if (giftCard.getStatus().equals(CouponStatus.USED)) {
            throw new UsedGiftCardException("Gift card is used!");
        }

        if (giftCard.getGiftAmount().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidGiftCardException("This gift card has no balance!");
        }
    }

    @Override
    public GiftCard findGiftCardById(Long id){
        return giftCardRepository.findById(id).orElseThrow(()-> new GiftCardNotFoundException("Gift Card not found"));
    }

    @Override
    public void compareGiftCardAmountWithOrderPrice(GiftCard giftCard, BigDecimal totalPrice){
        if (giftCard.getGiftAmount().getAmount().compareTo(totalPrice) > 0) {
            throw new InvalidGiftCardException("Gift card balance exceeds the total price.");
        }
    }

    @Override
    public void saveGiftCard(GiftCard giftCard) {
        giftCardRepository.save(giftCard);
    }
}
