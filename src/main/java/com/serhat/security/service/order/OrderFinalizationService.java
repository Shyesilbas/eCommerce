package com.serhat.security.service.order;

import com.serhat.security.entity.*;
import com.serhat.security.service.discountService.DiscountInterface;
import com.serhat.security.service.notification.NotificationInterface;
import com.serhat.security.service.product.ProductService;
import com.serhat.security.service.sCard.ShoppingCardService;
import com.serhat.security.service.user.UserInterface;
import com.serhat.security.service.bonusStrategy.BonusService;
import com.serhat.security.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFinalizationService {
    private final OrderRepository orderRepository;
    private final ShoppingCardService shoppingCardService;
    private final ProductService productService;
    private final NotificationInterface notificationInterface;
    private final DiscountInterface discountInterface;
    private final UserInterface userInterface;
    private final BonusService bonusService;

    @Transactional
    public void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        saveOrderAndUpdateUser(order, user);
        cleanupAfterOrder(shoppingCards, order.getOrderItems());
        handlePostOrderProcessing(request, order, order.getDiscountCode());
    }

    private void saveOrderAndUpdateUser(Order order, User user) {
        orderRepository.save(order);
        userInterface.updateUserTotalFees(user);
        updateUserBonus(user,order.getBonusWon());
        user.setTotalOrders(user.getTotalOrders() + 1);
        notificationInterface.addOrderCreationNotification(user, order);
    }

    public void updateUserBonus(User user , BigDecimal amount){
       bonusService.updateUserBonusPoints(user, amount);
    }

    private void cleanupAfterOrder(List<ShoppingCard> shoppingCards, List<OrderItem> orderItems) {
        shoppingCardService.clearShoppingCart(shoppingCards);
        productService.updateProductsAfterOrder(orderItems);
    }

    private void handlePostOrderProcessing(HttpServletRequest request, Order order, DiscountCode discountCode) {
        discountInterface.handleDiscountCode(request, order, discountCode);
    }
}
