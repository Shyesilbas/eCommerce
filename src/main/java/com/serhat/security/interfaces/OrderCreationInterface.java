package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.StockStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderCreationInterface {

    default void updateUserTotalFees(User user) {
        BigDecimal totalShippingFee = user.getOrders().stream()
                .map(Order::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOrderFee = user.getOrders().stream()
                .map(order -> order.getTotalPaid().subtract(order.getShippingFee()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaved = user.getOrders().stream()
                .map(Order::getTotalSaved)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        user.setTotalShippingFeePaid(totalShippingFee);
        user.setTotalOrderFeePaid(totalOrderFee);
        user.setTotalSaved(totalSaved);
    }

   default  void updateUserBonusPoints(User user, BigDecimal bonusPoints){
       user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
       user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
   }


    Order findOrderById(Long orderId);
    OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest);
    PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards , User user , OrderRequest orderRequest);
}
