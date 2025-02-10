package com.serhat.security.service;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.OrderCreationInterface;
import com.serhat.security.interfaces.PaymentServiceInterface;
import com.serhat.security.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentServiceInterface {

    private final WalletRepository walletRepository;
    private final DiscountCodeService discountService;
    private final GiftCardService giftCardService;
    private final TransactionService transactionService;
    private final ShippingService shippingService;
    private final BonusService bonusService;
    private final ShoppingCardService shoppingCardService;

    @Override
    public Wallet findWalletForUser(User user) {
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));
    }
    @Override
    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal totalPrice = shoppingCardService.calculateTotalPrice(shoppingCards);
        BigDecimal originalTotalPrice = totalPrice;
        BigDecimal shippingFee = shippingService.calculateShippingFee(user, totalPrice);
        BigDecimal bonusPoints = bonusService.calculateBonusPoints(user, totalPrice);

        if (orderRequest.discountId() != null && orderRequest.giftCardId() != null) {
            throw new InvalidOrderException("Cannot use both discount code and gift card in the same order.");
        }

        DiscountDetails discountDetails = discountService.applyDiscount(orderRequest, originalTotalPrice, user);
        totalPrice = totalPrice.subtract(discountDetails.discountAmount());

        GiftCard giftCard = giftCardService.applyGiftCard(orderRequest, totalPrice);
        if (giftCard != null) {
            totalPrice = totalPrice.subtract(giftCard.getGiftAmount().getAmount());
        }

        BonusUsageResult bonusUsageResult = bonusService.applyBonus(user, orderRequest, totalPrice);
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

    public void applyGiftCard(OrderRequest orderRequest , BigDecimal totalPrice){
        GiftCard giftCard = giftCardService.applyGiftCard(orderRequest, totalPrice);
        totalPrice = totalPrice.subtract(giftCard.getGiftAmount().getAmount());
    }

    @Override
    @Transactional
    public List<Transaction> createOrderTransactions(Order order) {
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }
    @Override
    public void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        PaymentServiceInterface.super.updateUserBonusPoints(user, bonusPoints);
    }
}

