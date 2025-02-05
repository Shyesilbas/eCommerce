package com.serhat.security.service;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.BonusUsageResult;
import com.serhat.security.dto.response.DiscountDetails;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.exception.*;
import com.serhat.security.repository.DiscountCodeRepository;
import com.serhat.security.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final WalletRepository walletRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final TransactionService transactionService;

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

        DiscountDetails discountDetails = applyDiscountIfAvailable(orderRequest, originalTotalPrice, user);
        totalPrice = totalPrice.subtract(discountDetails.discountAmount());

        BonusUsageResult bonusUsageResult = useBonusIfRequested(user, orderRequest, totalPrice);
        totalPrice = bonusUsageResult.updatedTotalPrice();
        BigDecimal bonusPointsUsed = bonusUsageResult.bonusPointsUsed();

        BigDecimal finalPrice = totalPrice.add(shippingFee);

        return new PriceDetails(totalPrice, originalTotalPrice, shippingFee, bonusPoints,
                discountDetails.discountAmount(), finalPrice, discountDetails.discountCode(), bonusPointsUsed);
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

    private BonusUsageResult useBonusIfRequested(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        BigDecimal bonusPointsUsed = BigDecimal.ZERO;

        if (orderRequest.useBonus() != null && orderRequest.useBonus()) {
            BigDecimal availableBonusPoints = user.getCurrentBonusPoints();
            if (availableBonusPoints.compareTo(BigDecimal.ZERO) > 0) {
                bonusPointsUsed = availableBonusPoints.min(totalPrice);
                totalPrice = totalPrice.subtract(bonusPointsUsed);

                user.setCurrentBonusPoints(availableBonusPoints.subtract(bonusPointsUsed));
                user.setTotalSaved(user.getTotalSaved().add(bonusPointsUsed));
            } else {
                throw new NoBonusPointsException("No bonus points found");
            }
        }

        return new BonusUsageResult(totalPrice, bonusPointsUsed);
    }

    public List<Transaction> createOrderTransactions(Order order) {
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }
}
