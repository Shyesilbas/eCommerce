package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.BonusInterface;
import com.serhat.security.interfaces.PaymentServiceInterface;
import com.serhat.security.interfaces.ShippingInterface;
import com.serhat.security.service.ShoppingCardService;
import com.serhat.security.service.TransactionService;
import com.serhat.security.service.discountService.DiscountCodeService;
import com.serhat.security.service.giftCard.GiftCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentServiceInterface {

    private final DiscountCodeService discountService;
    private final GiftCardService giftCardService;
    private final TransactionService transactionService;
    private final ShippingInterface shippingInterface;
    private final BonusInterface bonusInterface;
    private final ShoppingCardService shoppingCardService;

    public BigDecimal calculateShippingFee(User user , BigDecimal totalPrice){
        return shippingInterface.calculateShippingFee(user,totalPrice);
    }
    public void updateUserBonusPoints(User user,BigDecimal bonusPoints){
        bonusInterface.updateUserBonusPoints(user, bonusPoints);
    }
    public BigDecimal calculateBonusPoints(User user , BigDecimal totalPrice){
        return bonusInterface.calculateBonusPoints(user, totalPrice);
    }
    public BonusUsageResult applyBonus(User user , OrderRequest request , BigDecimal totalPrice){
        return bonusInterface.applyBonus(user, request, totalPrice);
    }

    @Override
    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal totalPrice = shoppingCardService.calculateTotalPrice(shoppingCards);
        BigDecimal originalTotalPrice = totalPrice;
        BigDecimal shippingFee = calculateShippingFee(user, totalPrice);
        BigDecimal bonusPoints = calculateBonusPoints(user, totalPrice);

        if (orderRequest.discountId() != null && orderRequest.giftCardId() != null) {
            throw new InvalidOrderException("Cannot use both discount code and gift card in the same order.");
        }

        DiscountDetails discountDetails = discountService.applyDiscount(orderRequest, originalTotalPrice, user);
        totalPrice = totalPrice.subtract(discountDetails.discountAmount());

        GiftCard giftCard = giftCardService.applyGiftCard(orderRequest, totalPrice);
        if (giftCard != null) {
            totalPrice = totalPrice.subtract(giftCard.getGiftAmount().getAmount());
        }

        BonusUsageResult bonusUsageResult = applyBonus(user, orderRequest, totalPrice);
        totalPrice = bonusUsageResult.updatedTotalPrice();
        BigDecimal bonusPointsUsed = bonusUsageResult.bonusPointsUsed();
        updateUserBonusPoints(user, bonusPoints);

        BigDecimal finalPrice = totalPrice.add(shippingFee);

        BigDecimal totalSaved = discountDetails.discountAmount().add(bonusPointsUsed);

        return new PriceDetails(
                totalPrice,
                originalTotalPrice,
                shippingFee,
                bonusPoints,
                discountDetails.discountAmount(),
                finalPrice,
                discountDetails.discountCode(),
                bonusPointsUsed,
                totalSaved
        );
    }

    @Override
    @Transactional
    public List<Transaction> createOrderTransactions(Order order) {
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }
}

