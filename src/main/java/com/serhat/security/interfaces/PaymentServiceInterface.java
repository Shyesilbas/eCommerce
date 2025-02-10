package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentServiceInterface {
    PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest);
    List<Transaction> createOrderTransactions(Order order);

    Wallet findWalletForUser(User user);
    default  void updateUserBonusPoints(User user, BigDecimal bonusPoints){
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
    }

}
