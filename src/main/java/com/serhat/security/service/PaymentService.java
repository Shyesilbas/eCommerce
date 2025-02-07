package com.serhat.security.service;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.exception.*;
import com.serhat.security.repository.DiscountCodeRepository;
import com.serhat.security.repository.GiftCardRepository;
import com.serhat.security.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final WalletRepository walletRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final TransactionService transactionService;
    private final GiftCardRepository giftCardRepository;
    public Wallet findWalletForUser(User user) {
      return   walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));
    }
    public BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        switch (user.getMembershipPlan()) {
            case VIP -> {
                return BigDecimal.ZERO;
            }
            case PREMIUM -> {
                return (totalPrice.compareTo(BigDecimal.valueOf(100)) >= 0) ? BigDecimal.ZERO : BigDecimal.valueOf(6.99);
            }
            case BASIC -> {
                return (totalPrice.compareTo(BigDecimal.valueOf(200)) >= 0) ? BigDecimal.ZERO : BigDecimal.valueOf(10.99);
            }
        }
        return BigDecimal.ZERO;
    }
    private BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        BigDecimal bonusRate = switch (user.getMembershipPlan()) {
            case VIP -> BigDecimal.valueOf(0.05);
            case PREMIUM -> BigDecimal.valueOf(0.03);
            case BASIC -> BigDecimal.valueOf(0.01);
        };
        return totalPrice.multiply(bonusRate);
    }

    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal totalPrice = calculateTotalPrice(shoppingCards);
        BigDecimal originalTotalPrice = totalPrice;
        BigDecimal shippingFee = calculateShippingFee(user, totalPrice);
        BigDecimal bonusPoints = calculateBonusPoints(user, totalPrice);

        if (orderRequest.discountId() != null && orderRequest.giftCardId() != null) {
            throw new InvalidOrderException("Cannot use both discount code and gift card in the same order.");
        }

        DiscountDetails discountDetails = applyDiscountIfAvailable(orderRequest, originalTotalPrice, user);
        totalPrice = totalPrice.subtract(discountDetails.discountAmount());

        GiftCard giftCard = applyGiftCardIfAvailable(orderRequest, totalPrice);
        if (giftCard != null) {
            totalPrice = totalPrice.subtract(giftCard.getGiftAmount().getAmount());
        }

        BonusUsageResult bonusUsageResult = useBonusIfRequested(user, orderRequest, totalPrice);
        totalPrice = bonusUsageResult.updatedTotalPrice();
        BigDecimal bonusPointsUsed = bonusUsageResult.bonusPointsUsed();

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


    private BigDecimal calculateTotalPrice(List<ShoppingCard> shoppingCards) {
        return shoppingCards.stream()
                .map(sc -> sc.getProduct().getPrice().multiply(new BigDecimal(sc.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private DiscountDetails applyDiscountIfAvailable(OrderRequest orderRequest, BigDecimal originalTotalPrice, User user) {
        if (orderRequest.discountId() == null) {
            return new DiscountDetails(BigDecimal.ZERO, null);
        }

        DiscountCode discountCode = discountCodeRepository.findById(orderRequest.discountId())
                .orElseThrow(() -> new InvalidDiscountCodeException("Invalid discount code"));

        validateDiscountCode(discountCode, user);
        BigDecimal discountAmount = calculateDiscountAmount(originalTotalPrice, discountCode);
        return new DiscountDetails(discountAmount, discountCode);
    }

    private BigDecimal calculateDiscountAmount(BigDecimal originalTotalPrice, DiscountCode discountCode) {
        return originalTotalPrice
                .multiply(BigDecimal.valueOf(discountCode.getDiscountRate().getPercentage() / 100.0));
    }

    private void validateDiscountCode(DiscountCode discountCode, User user) {
        if (discountCode.getUser() != null && !discountCode.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidDiscountCodeException("This discount code is not valid for the current user!");
        }

        if (discountCode.getStatus().equals(CouponStatus.EXPIRED)) {
            throw new DiscountCodeExpiredException("This coupon has expired!");
        }

        if (discountCode.getStatus().equals(CouponStatus.USED)) {
            throw new CouponAlreadyUsedException("The coupon you entered is already used");
        }
    }

    private GiftCard applyGiftCardIfAvailable(OrderRequest orderRequest, BigDecimal totalPrice) {
        if (orderRequest.giftCardId() == null) {
            return null;
        }

        GiftCard giftCard = giftCardRepository.findById(orderRequest.giftCardId())
                .orElseThrow(() -> new InvalidGiftCardException("Invalid gift card"));

        validateGiftCard(giftCard);
        if (giftCard.getGiftAmount().getAmount().compareTo(totalPrice) > 0) {
            throw new InvalidGiftCardException("Gift card balance exceeds the total price.");
        }

        giftCard.setStatus(CouponStatus.USED);
        giftCardRepository.save(giftCard);
        return giftCard;
    }

    public void validateGiftCard(GiftCard giftCard) {
        if (giftCard.getStatus().equals(CouponStatus.USED)){
            throw new UsedGiftCardException("Gift cart is used!");
        }

        if (giftCard.getGiftAmount().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidGiftCardException("This gift card has no balance!");
        }
    }

    private BonusUsageResult useBonusIfRequested(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        BigDecimal bonusPointsUsed = BigDecimal.ZERO;

        if (orderRequest.useBonus() != null && orderRequest.useBonus()) {
            BigDecimal availableBonusPoints = user.getCurrentBonusPoints();

            if (availableBonusPoints.compareTo(BigDecimal.ZERO) > 0) {
                bonusPointsUsed = availableBonusPoints.min(totalPrice);
                totalPrice = totalPrice.subtract(bonusPointsUsed);

                user.setCurrentBonusPoints(availableBonusPoints.subtract(bonusPointsUsed));
            } else {
                throw new NoBonusPointsException("No bonus points found");
            }
        }

        return new BonusUsageResult(totalPrice, bonusPointsUsed);
    }

    @Transactional
    public List<Transaction> createOrderTransactions(Order order) {
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }
}
