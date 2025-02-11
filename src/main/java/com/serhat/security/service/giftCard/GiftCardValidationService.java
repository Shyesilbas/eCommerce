package com.serhat.security.service.giftCard;

import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.exception.GiftCardNotFoundException;
import com.serhat.security.exception.InvalidGiftCardException;
import com.serhat.security.exception.UsedGiftCardException;
import com.serhat.security.interfaces.GiftCardValidationInterface;
import com.serhat.security.repository.GiftCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class GiftCardValidationService implements GiftCardValidationInterface {
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
}
