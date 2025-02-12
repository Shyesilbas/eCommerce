package com.serhat.security.service.payment;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.interfaces.*;
import com.serhat.security.service.BonusService;
import com.serhat.security.service.ShippingService;
import com.serhat.security.service.ShoppingCardService;
import com.serhat.security.service.TransactionService;
import com.serhat.security.service.giftCard.GiftCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentServiceInterface {

    private final DiscountInterface discountInterface;
    private final GiftCardService giftCardService;
    private final TransactionService transactionService;
    private final ShippingService shippingService;
    private final BonusService bonusService;
    private final ShoppingCardService shoppingCardService;
    private final PaymentRulesService paymentRulesService;


    public BigDecimal calculateShippingFee(User user , BigDecimal totalPrice){
        return shippingService.calculateShippingFee(user,totalPrice);
    }
    public void updateUserBonusPoints(User user,BigDecimal bonusPoints){
        bonusService.updateUserBonusPoints(user, bonusPoints);
    }
    public BigDecimal calculateBonusPoints(User user , BigDecimal totalPrice){
        return bonusService.calculateBonusPoints(user, totalPrice);
    }
    public BonusUsageResult applyBonus(User user , OrderRequest request , BigDecimal totalPrice){
        return bonusService.applyBonus(user, request, totalPrice);
    }
    public GiftCard applyGiftCard(OrderRequest orderRequest , BigDecimal totalPrice){
       return giftCardService.applyGiftCard(orderRequest, totalPrice);
    }
    public DiscountDetails applyDiscountCode(OrderRequest orderRequest , BigDecimal originalPrice,User user){
       return discountInterface.applyDiscount(orderRequest, originalPrice, user);
    }
    public  BigDecimal calculateTotalPrice(List<ShoppingCard> shoppingCards){
       return shoppingCardService.calculateTotalPrice(shoppingCards);
    }
    public void checkIfBothDiscountCodeAndGiftCardIsNotUsed(OrderRequest orderRequest){
        paymentRulesService.checkIfBothDiscountCodeAndGiftCardIsNotUsed(orderRequest);
    }

    @Override
    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal totalPrice = calculateTotalPrice(shoppingCards);
        BigDecimal originalTotalPrice = totalPrice;
        BigDecimal shippingFee = calculateShippingFee(user, totalPrice);
        BigDecimal bonusPoints = calculateBonusPoints(user, totalPrice);

        checkIfBothDiscountCodeAndGiftCardIsNotUsed(orderRequest);

        DiscountDetails discountDetails = applyDiscountCode(orderRequest, totalPrice, user);
        totalPrice = totalPrice.subtract(discountDetails.discountAmount());

        GiftCard giftCard = applyGiftCard(orderRequest, totalPrice);
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

