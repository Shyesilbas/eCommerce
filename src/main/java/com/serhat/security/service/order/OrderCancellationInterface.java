package com.serhat.security.service.order;

import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.entity.enums.StockStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface OrderCancellationInterface {
    default void updateUserAfterOrderCancel(User user, Order order, BigDecimal shippingFee, BigDecimal totalPaid) {
        user.setCancelledOrders(user.getCancelledOrders() + 1);
        user.setTotalShippingFeePaid(user.getTotalShippingFeePaid().subtract(shippingFee));
        user.setTotalOrderFeePaid(user.getTotalOrderFeePaid().subtract(totalPaid));
        user.setBonusPointsWon(user.getBonusPointsWon().subtract(order.getBonusWon()));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().subtract(order.getBonusWon()));
        user.setTotalSaved(user.getTotalSaved().subtract(order.getTotalSaved()));
    }
    default void updateProductStockAfterCancellation(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());

            if (product.getQuantity() > 0 && product.getStockStatus() == StockStatus.OUT_OF_STOCKS) {
                product.setStockStatus(StockStatus.AVAILABLE);
            }
        }
    }
    void checkIsOrderCancellable(Order order , User user);
    OrderCancellationResponse cancelOrder(Long orderId, HttpServletRequest request);
    void finalizeCancellation(Order order, User user);
    void updateOrderAfterCancellation(Order order);
    void processRefundPayment(Order order, PaymentMethod paymentMethod);
}
